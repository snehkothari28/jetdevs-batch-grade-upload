package com.jetdevs.batchgradeupload.model;

/**
 * Enumeration representing the status of an uploaded file.
 */
public enum FileStatus {
    /**
     * Status indicating that the file has been successfully uploaded.
     */
    UPLOADED,

    /**
     * Status indicating that the file data has been processed and stored.
     */
    DUMPED,

    /**
     * Status indicating that the file is in the process of being deleted.
     */
    DELETING,

    /**
     * Status indicating that an error occurred during processing.
     */
    ERROR;

    /**
     * Returns a human-readable description of the file status.
     *
     * @return String representing the file status description.
     */
    public String getFileStatus() {
        return switch (this) {
            case ERROR -> "Error encountered";
            case UPLOADED -> "File under Processing";
            case DUMPED -> "File successfully Processed";
            case DELETING -> "Deleting file";
        };
    }
}
