package de.mc8051.arma3launcher.repo.sync;

import co.bitshfted.xapps.zsync.Zsync;
import co.bitshfted.xapps.zsync.http.ContentRange;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by gurkengewuerz.de on 29.03.2020.
 */
public interface SyncListener {

    public void bytesDownloaded(long bytes);
    public void remoteFileDownloadingInitiated(List<ContentRange> ranges);
    public void remoteFileDownloadingStarted(long length);
    public void remoteFileDownloadingComplete();
    public void controlFileDownloadingComplete();
    public void controlFileDownloadingStarted(Path path, long length);
    public void controlFileReadingComplete();
    public void outputFileWritingStarted(long length);
    public void outputFileWritingCompleted();
    public void inputFileReadingStarted(Path inputFile, long length);
    public void inputFileReadingComplete();
    public void zsyncComplete();
    public void zsyncFailed(Exception exception);
    public void zsyncStarted(Zsync.Options options);
}
