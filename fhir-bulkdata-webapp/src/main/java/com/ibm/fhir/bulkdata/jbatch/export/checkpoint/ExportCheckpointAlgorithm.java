/*
 * (C) Copyright IBM Corp. 2019, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.bulkdata.jbatch.export.checkpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.chunk.CheckpointAlgorithm;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ibm.fhir.bulkdata.jbatch.context.BatchContextAdapter;
import com.ibm.fhir.bulkdata.jbatch.export.data.ExportTransientUserData;
import com.ibm.fhir.operation.bulkdata.config.ConfigurationAdapter;
import com.ibm.fhir.operation.bulkdata.config.ConfigurationFactory;
import com.ibm.fhir.operation.bulkdata.model.type.BulkDataContext;

/**
 * BulkData Export Custom CheckpointAlgorithm which considers COS size requirements while checkpointing.
 */
@Dependent
public class ExportCheckpointAlgorithm implements CheckpointAlgorithm {

    private final static Logger logger = Logger.getLogger(ExportCheckpointAlgorithm.class.getName());

    @Inject
    JobContext jobCtx;

    @Inject
    StepContext stepCtx;

    Boolean isFileExport;

    ConfigurationAdapter adapter;

    public ExportCheckpointAlgorithm() {
        // No Operation
    }

    @Override
    public int checkpointTimeout() {
        return 0;
    }

    @Override
    public void beginCheckpoint() {
        if (isFileExport == null) {
            JobOperator jobOperator = BatchRuntime.getJobOperator();
            JobExecution jobExecution = jobOperator.getJobExecution(jobCtx.getExecutionId());
            BatchContextAdapter contextAdapter = new BatchContextAdapter(jobExecution.getJobParameters());
            BulkDataContext ctx = contextAdapter.getStepContextForSystemChunkWriter();
            isFileExport = "file".equals(ConfigurationFactory.getInstance().getStorageProviderType(ctx.getSource()));
        }

        if (adapter == null) {
            adapter = ConfigurationFactory.getInstance();
        }

        if (logger.isLoggable(Level.FINE)) {
            ExportTransientUserData chunkData = (ExportTransientUserData) stepCtx.getTransientUserData();
            if (chunkData != null) {
                logger.fine("begin checkpoint [" +
                        "page " + chunkData.getPageNum() + " of " + chunkData.getLastPageNum() + ", " +
                        "bufferSize=" + chunkData.getBufferStream().size() + ", " +
                        "uploadPart=" + chunkData.getPartNum() + ", " +
                        "currentUploadSize=" + chunkData.getCurrentUploadSize() + ", " +
                        "currentUploadResourceNum=" + chunkData.getCurrentUploadResourceNum() +
                        "]");
            }
        }
    }

    @Override
    public void endCheckpoint() {
        if (logger.isLoggable(Level.FINE)) {
            ExportTransientUserData chunkData = (ExportTransientUserData) stepCtx.getTransientUserData();
            if (chunkData == null) {
                logger.warning("end checkpoint [no chunkData]");
            } else {
                logger.fine("end checkpoint [" +
                        "page " + chunkData.getPageNum() + " of " + chunkData.getLastPageNum() + ", " +
                        "bufferSize=" + chunkData.getBufferStream().size() + ", " +
                        "uploadPart=" + chunkData.getPartNum() + ", " +
                        "currentUploadSize=" + chunkData.getCurrentUploadSize() + ", " +
                        "currentUploadResourceNum=" + chunkData.getCurrentUploadResourceNum() +
                        "]");
            }
        }
    }

    @Override
    public boolean isReadyToCheckpoint() {
        ExportTransientUserData chunkData = (ExportTransientUserData) stepCtx.getTransientUserData();
        if (chunkData == null) {
            return false;
        }

        long resourceCountThreshold = isFileExport ? adapter.getCoreFileResourceCountThreshold()
                : adapter.getCoreCosObjectResourceCountThreshold();
        long sizeThreshold = isFileExport ? adapter.getCoreFileSizeThreshold()
                : adapter.getCoreCosObjectSizeThreshold();
        long writeTrigger = isFileExport ? adapter.getCoreFileWriteTriggerSize()
                : adapter.getCoreCosPartUploadTriggerSize();

        // Set to true if we have enough bytes to write a part
        boolean readyToWrite = chunkData.getBufferStream().size() >= writeTrigger;

        // Check if we should finish writing the current object/file
        boolean overFileSizeThreshold = sizeThreshold != 0 && chunkData.getBufferStream().size() >= sizeThreshold;
        boolean overMaxResourceCountThreshold = resourceCountThreshold != 0 && chunkData.getCurrentUploadResourceNum() >= resourceCountThreshold;
        boolean end = chunkData.getPageNum() >= chunkData.getLastPageNum();
        chunkData.setFinishCurrentUpload(overFileSizeThreshold || overMaxResourceCountThreshold || end);

        // There are two conditions that trigger a checkpoint:
        // 1 - We have enough bytes to start writing a part; or
        // 2 - We're ready to finish writing the current object/file
        return readyToWrite || chunkData.isFinishCurrentUpload();
    }
}