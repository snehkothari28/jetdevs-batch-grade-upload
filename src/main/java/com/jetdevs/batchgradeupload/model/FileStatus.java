package com.jetdevs.batchgradeupload.model;

public enum FileStatus {
    Uploaded,
    Dumped,
    Deleting,
    Deleted,
    Error;

    public String getFileStatus() {
        return switch (this) {
            case Error -> "Error encountered";
            case Uploaded -> "File under Processing";
            case Dumped -> "File successfully Processed";
            case Deleting -> "Deleting file";
            case Deleted -> "Deleted file";
        };
    }
}
