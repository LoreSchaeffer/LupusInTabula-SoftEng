package it.multicoredev.client.ui.cef;

import it.multicoredev.utils.LitLogger;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LocalSchemeHandler extends CefResourceHandlerAdapter {
    public static final String SCHEME = "local";
    private static final String PATH = "/assets/";

    private byte[] data;
    private String mimeType;
    private int offset = 0;

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        boolean handled = false;
        String url = request.getURL().substring(SCHEME.length() + 3);
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        String extension = url.substring(url.lastIndexOf(".") + 1);

        String path = PATH + url;

        switch (extension) {
            case "html":
                if (loadTextFile(path)) {
                    mimeType = "text/html";
                    handled = true;
                }
                break;
            case "css":
                if (loadTextFile(path)) {
                    mimeType = "text/css";
                    handled = true;
                }
                break;
            case "js":
                if (loadTextFile(path)) {
                    mimeType = "text/javascript";
                    handled = true;
                }
                break;
            case "png":
                if (loadFile(path)) {
                    mimeType = "image/png";
                    handled = true;
                }
                break;
            case "jpg":
                if (loadFile(path)) {
                    mimeType = "image/jpg";
                    handled = true;
                }
                break;
            default:
                LitLogger.get().warn("Unknown extension: " + extension);
                break;
        }

        if (handled) {
            callback.Continue();
            return true;
        }

        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        response.setMimeType(mimeType);
        response.setStatus(200);
        responseLength.set(data.length);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        boolean hasData = false;

        if (offset < data.length) {
            int transferSize = Math.min(bytesToRead, (data.length - offset));
            System.arraycopy(data, offset, dataOut, 0, transferSize);
            offset += transferSize;

            bytesRead.set(transferSize);
            hasData = true;
        } else {
            offset = 0;
            bytesRead.set(0);
        }

        return hasData;
    }

    private boolean loadTextFile(String path) {
        File file = new File(path.substring(1));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.exists() && file.isFile() ? new FileInputStream(file) : Objects.requireNonNull(LocalSchemeHandler.class.getResourceAsStream(path)), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            data = content.toString().getBytes();
            return true;
        } catch (IOException | NullPointerException e) {
            LitLogger.get().error("Failed to load resource '" + path + "'", e);
            return false;
        }
    }

    private boolean loadFile(String path) {
        File file = new File(path.substring(1));

        try (InputStream is = file.exists() && file.isFile() ? new FileInputStream(file) : Objects.requireNonNull(getClass().getResourceAsStream(path))) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int readByte;

            while ((readByte = is.read()) != -1) {
                os.write(readByte);
            }

            data = os.toByteArray();
            return true;
        } catch (IOException | NullPointerException e) {
            LitLogger.get().error("Failed to load resource '" + path + "'", e);
            return false;
        }
    }
}
