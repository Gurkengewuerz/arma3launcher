package de.mc8051.arma3launcher.repo.sync;

import co.bitshfted.xapps.zsync.Zsync;
import co.bitshfted.xapps.zsync.ZsyncStatsObserver;
import co.bitshfted.xapps.zsync.http.ContentRange;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public class SyncObserver extends ZsyncStatsObserver {

    private final SyncListener listener;

    public SyncObserver(SyncListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void zsyncStarted(URI requestedZsyncUri, Zsync.Options options) {
        super.zsyncStarted(requestedZsyncUri, options);
        listener.zsyncStarted(options);
    }

    @Override
    public void zsyncComplete() {
        super.zsyncComplete();
        listener.zsyncComplete();
    }

    @Override
    public void zsyncFailed(Exception exception) {
        super.zsyncFailed(exception);
        listener.zsyncFailed(exception);
    }

    @Override
    public void inputFileReadingStarted(Path inputFile, long length) {
        super.inputFileReadingStarted(inputFile, length);
        listener.inputFileReadingStarted(inputFile, length);
    }

    @Override
    public void inputFileReadingComplete() {
        super.inputFileReadingComplete();
        listener.inputFileReadingComplete();
    }

    @Override
    public void controlFileDownloadingComplete() {
        super.controlFileDownloadingComplete();
        listener.controlFileDownloadingComplete();
    }

    @Override
    public void controlFileReadingStarted(Path path, long length) {
        super.controlFileReadingStarted(path, length);
        listener.controlFileDownloadingStarted(path, length);
    }

    @Override
    public void controlFileReadingComplete() {
        super.controlFileReadingComplete();
        listener.controlFileReadingComplete();
    }

    @Override
    public void outputFileWritingStarted(Path outputFile, long length) {
        super.outputFileWritingStarted(outputFile, length);
        listener.outputFileWritingStarted(length);
    }

    @Override
    public void outputFileWritingCompleted() {
        super.outputFileWritingCompleted();
        listener.outputFileWritingCompleted();
    }

    @Override
    public void remoteFileDownloadingInitiated(URI uri, List<ContentRange> ranges) {
        super.remoteFileDownloadingInitiated(uri, ranges);
        listener.remoteFileDownloadingInitiated(ranges);
    }

    @Override
    public void remoteFileDownloadingStarted(URI uri, long length) {
        super.remoteFileDownloadingStarted(uri, length);
        listener.remoteFileDownloadingStarted(length);
    }

    @Override
    public void remoteFileDownloadingComplete() {
        super.remoteFileDownloadingComplete();
        listener.remoteFileDownloadingComplete();
    }

    @Override
    public void bytesDownloaded(long bytes) {
        super.bytesDownloaded(bytes);
        listener.bytesDownloaded(bytes);
    }

    @Override
    public void bytesToDownload(long bytes) {
        super.bytesToDownload(bytes);
        listener.bytesToDownload(bytes);
    }

    @Override
    public void downloaded(long bytes) {
        super.downloaded(bytes);
        listener.downloaded(bytes);
    }
}
