/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.httpserver;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.frostwire.core.FileDescriptor;
import com.frostwire.gui.Librarian;
import com.frostwire.gui.bittorrent.BTDownloadMediator;
import com.frostwire.gui.transfers.PeerHttpUpload;
import com.frostwire.logging.Logger;
import com.frostwire.util.URLUtils;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author gubatron
 * @author aldenml
 *
 */
class DownloadHandler extends AbstractHandler {

    private static final Logger LOG = Logger.getLogger(DownloadHandler.class);

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    internalHandler(exchange);
                } catch (IOException e) {
                    LOG.warn("DownloadHandler async handle error", e);
                }
            }
        }).start();
    }
    
    public void internalHandler(HttpExchange exchange) throws IOException {
        assertUPnPActive();

        OutputStream os = null;
        FileInputStream fis = null;

        byte type = -1;
        int id = -1;

        PeerHttpUpload upload = null;

        try {
            
            Map<String, String> splitQuery = URLUtils.splitQuery(exchange.getRequestURI().getQuery());

            if (splitQuery.containsKey("type")) {
                type = Byte.parseByte(splitQuery.get("type"));
            }
            
            if (splitQuery.containsKey("id")) {
                id = Integer.parseInt(splitQuery.get("id"));
            }
            
            if (type == -1 || id == -1) {
                exchange.sendResponseHeaders(Code.HTTP_BAD_REQUEST, 0);
                return;
            }

            //if (TransferManager.instance().getActiveUploads() >= ConfigurationManager.instance().maxConcurrentUploads()) {
            //    sendBusyResponse(exchange);
            //    return;
            //}

            FileDescriptor fd = Librarian.instance().getSharedFileDescriptor(type, id);
            if (fd == null) {
                throw new IOException("There is no such file shared");
            }

            //upload = TransferManager.instance().upload(fd);
            upload = BTDownloadMediator.instance().upload(fd);

            exchange.getResponseHeaders().add("Content-Type", fd.mime);
            
            File file = new File(fd.filePath);
            
            exchange.sendResponseHeaders(Code.HTTP_OK, file.length());

            os = exchange.getResponseBody();

            fis = new FileInputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int n;
            int count = 0;

            while ((n = fis.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, n);
                upload.addBytesSent(n);
                
                if (upload.isCanceled()) {
                    try {
                        throw new IOException("Upload cancelled");
                    } finally {
                        os.close();
                    }
                }
                
                count += n;
                if (count > 4096) {
                    count = 0;
                    Thread.yield();
                }
            }

        } catch (IOException e) {
            LOG.info("Error uploading file type=" + type + ", id=" + id);
            throw e;
        } finally {
            close(os);
            close(fis);

            try {
                exchange.close();
            } catch (Throwable e) {
                // ignore
            }

            if (upload != null) {
                upload.complete();
            }
        }
    }

    private void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    //    private void sendBusyResponse(HttpExchange exchange) throws IOException {
    //        exchange.getResponseHeaders().add("Retry-After", "10"); // retry in 10 seconds
    //        exchange.sendResponseHeaders(Code.HTTP_UNAVAILABLE, 0);
    //    }
}
