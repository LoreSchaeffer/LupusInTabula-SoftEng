package it.multicoredev.client.ui.cef;

import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalSchemeHandler extends CefResourceHandlerAdapter {
    public static final String SCHEME = "local";
    private static final String PATH = "assets/";
    private static final Pattern INCLUDE_PATTERN = Pattern.compile("<!--include\\(.*\\)-->");

    private byte[] data;
    private String mimeType;
    private int offset = 0;

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        if (Static.DEBUG) LitLogger.info(request.getMethod() + " : " + request.getURL());

        boolean handled = false;
        String url = request.getURL().substring(SCHEME.length() + 3);
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        String extension = url.substring(url.lastIndexOf(".") + 1);

        String path = url;

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
            case "map":
                if (loadTextFile(path)) {
                    mimeType = "application/json";
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
            case "svg":
                if (loadTextFile(path)) {
                    mimeType = "image/svg+xml";
                    handled = true;
                }
            case "ttf":
                if (loadFile(path)) {
                    mimeType = "application/octet-stream";
                    handled = true;
                }
                break;
            case "woff2":
                if (loadFile(path)) {
                    mimeType = "application/octet-stream";
                    handled = true;
                }
                break;
            default:
                LitLogger.warn("Unknown extension: " + extension);
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
        if (data == null || data.length == 0) {
            response.setStatus(404);
            responseLength.set(0);
            return;
        }

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

    @Nullable
    private String readTextFile(String path) {
        File file = new File(path);
        boolean loadFromFile = file.exists() && file.isFile();

        if (Static.DEBUG) LitLogger.info("Loading " + path + " from " + (loadFromFile ? "file" : "jar"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(loadFromFile ? new FileInputStream(file) : Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = INCLUDE_PATTERN.matcher(line);
                if (matcher.find()) {
                    String include = matcher.group();
                    include = include.substring(include.indexOf("(") + 1, include.indexOf(")"));
                    include = PATH + (include.startsWith("/") ? include.substring(1) : include);

                    String included = readTextFile(include);
                    if (included != null) content.append(included).append("\n");
                    continue;
                }

                content.append(line).append("\n");
            }

            return content.toString();
        } catch (IOException | NullPointerException e) {
            LitLogger.error("Failed to load resource '" + path + "': " + e.getMessage());
            return null;
        }
    }

    private boolean loadTextFile(String path) {
        String content = readTextFile(path);
        if (content == null) return false;

        data = content.getBytes();
        return true;
    }

    private boolean loadFile(String path) {
        File file = new File(path);
        boolean loadFromFile = file.exists() && file.isFile();

        if (Static.DEBUG) LitLogger.info("Loading " + path + " from " + (loadFromFile ? "file" : "jar"));

        try (InputStream is = loadFromFile ? new FileInputStream(file) : Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path))) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int readByte;

            while ((readByte = is.read()) != -1) {
                os.write(readByte);
            }

            data = os.toByteArray();
            return true;
        } catch (IOException | NullPointerException e) {
            LitLogger.error("Failed to load resource '" + path + "'", e);
            return false;
        }
    }
}
