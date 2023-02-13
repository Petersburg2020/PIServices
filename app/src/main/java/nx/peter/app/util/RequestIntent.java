package nx.peter.app.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import nx.peter.java.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestIntent {
    public static final int FILES = 100;
    public static final int IMAGE = 98;
    public static final int AUDIO = 99;
    public static final int VIDEO = 97;

    protected RequestIntent() {
    }

    public static Requester fromActivity(RequestActivity act) {
        return new IRequester(act);
    }

    private static class IRequester implements Requester {
        protected RequestActivity act;
        protected List<Request> requests;

        public IRequester(RequestActivity act) {
            this.act = act;
            requests = new ArrayList<>();
        }

        @Override
        public Requester chooseAudio(OnResultListener listener) {
            Intent intent = chooseFile("audio/*", "Choose an audio");
            return makeRequest(AUDIO, intent, listener);
        }

        @Override
        public Requester chooseImage(OnResultListener listener) {
            Intent intent = chooseFile("image/*", "Choose an image");
            return makeRequest(IMAGE, intent, listener);
        }

        @Override
        public Requester chooseVideo(OnResultListener listener) {
            Intent intent = chooseFile("video/*", "Choose a video");
            return makeRequest(VIDEO, intent, listener);
        }

        @Override
        public Requester chooseFile(OnResultListener listener) {
            Intent intent = chooseFile("*/*", "Choose a file");
            return makeRequest(FILES, intent, listener);
        }

        protected Intent chooseFile(String mime, String message) {
            return Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType(mime), message);
        }

        @Override
        public Requester makeRequest(final int code, final Intent intent, final OnResultListener listener) {
            Request request = new Request() {
                @Override
                public int getCode() {
                    return code;
                }

                @Override
                public Intent getIntent() {
                    return intent;
                }

                @Override
                public OnResultListener getListener() {
                    return listener;
                }
            };
            if (!new Requests(requests).contains(request)) requests.add(request);
            act.addRequest(request);
            return this;
        }

        @Override
        public void initiate() {
            for (Request r : requests) act.startActivityForResult(r.getIntent(), r.getCode());
        }
    }

    public interface Requester {
        void initiate();

        Requester chooseAudio(OnResultListener listener);

        Requester chooseFile(OnResultListener listener);

        Requester chooseImage(OnResultListener listener);

        Requester chooseVideo(OnResultListener listener);

        Requester makeRequest(final int code, final Intent intent, final OnResultListener listener);
    }

    protected interface Coded {
        Intent getIntent();

        int getCode();
    }

    public interface Request extends Coded {

        OnResultListener getListener();
    }

    public static class Results extends Array<Result> {
        public Results(List<Result> results) {
            super(results);
        }
    }

    protected static class Array<T extends Coded> extends Document.Array<T> {
        public Array(List<T> items) {
            super(items);
        }

        @Override
        public T get(int code) {
            for (T item : items) if (item.getCode() == code) return item;
            return null;
        }

        public boolean contains(int code) {
            for (T i : items) if (i.getCode() == code) return true;
            return false;
        }

        @Override
        public boolean contains(T item) {
            return item != null && contains(item.getCode());
        }
    }

    public interface Media {
        Uri getUri();

        String getPath();

        Type getType();

        enum Type {
            Audio,
            Image,
            Video
        }
    }

    public interface Image extends Media {
        Bitmap getBitmap();
    }

    public interface Audio extends Media {
        Bitmap getBitmap();
    }

    public interface Result extends Coded {
        Request getRequest();

        Type getType();

        Feedback getFeedback();

        enum Feedback {
            Denied,
            Success
        }

        enum Type {
            Audio,
            Image,
            Others,
            Video
        }
    }

    public interface MediaResult extends Result {
        ContentResolver getResolver();

        Media getMedia();
    }

    public static class Requests extends Array<Request> {
        public Requests(List<Request> requests) {
            super(requests);
        }
    }

    public interface ResultSuccess extends Result {
        Uri getData();
    }

    public interface ResultDenied extends Result {
        String getCause();
    }

    public interface ImageSuccess extends MediaResult, ResultSuccess {
        Image getMedia();
    }

    public interface AudioSuccess extends MediaResult, ResultSuccess {
        Audio getMedia();
    }

    public interface OnResultListener {
        void onActivityResult(Result result);
    }

    public abstract static class RequestActivity extends AppCompatActivity {
        private List<Result> results;
        private List<Request> requests;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            results = new ArrayList<>();
            requests = new ArrayList<>();
        }

        public void addRequest(Request request) {
            if (!requests.contains(request)) requests.add(request);
        }

        private String path;
        private ContentResolver resolver;
        private Bitmap bitmap;

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (!requests.isEmpty())
                for (Request request : requests) {
                    if (request.getCode() == requestCode) {
                        Result result = null;
                        path = "";
                        resolver = getContentResolver();
                        Uri uri = intent.getData();
                        if (resultCode == RESULT_OK)
                            if (requestCode == IMAGE) {
                                String[] imageProjection = {MediaStore.Images.Media.DATA};
                                Cursor cursor = resolver.query(uri, imageProjection, null, null, null);
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    int indexImage = cursor.getColumnIndex(imageProjection[0]);
                                    // Get the image file absolute path
                                    path = cursor.getString(indexImage);
                                    bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    cursor.close();
                                }
                                result = new ImageSuccess() {
                                            @Override
                                            public int getCode() {
                                                return resultCode;
                                            }

                                            @Override
                                            public Uri getData() {
                                                return uri;
                                            }

                                            @Override
                                            public Request getRequest() {
                                                return request;
                                            }

                                            @Override
                                            public Intent getIntent() {
                                                return intent;
                                            }

                                            @Override
                                            public Type getType() {
                                                return Type.Image;
                                            }

                                            @Override
                                            public Feedback getFeedback() {
                                                return Feedback.Success;
                                            }

                                            @Override
                                            public Image getMedia() {
                                                return new Image() {

                                                    @Override
                                                    public Bitmap getBitmap() {
                                                        return bitmap;
                                                    }

                                                    @Override
                                                    public Type getType() {
                                                        return Type.Image;
                                                    }

                                                    @Override
                                                    public String getPath() {
                                                        return path;
                                                    }

                                                    @Override
                                                    public Uri getUri() {
                                                        return uri;
                                                    }
                                                };
                                            }

                                            @Override
                                            public ContentResolver getResolver() {
                                                return resolver;
                                            }
                                        };
                            } else {
                                result = new ResultSuccess() {
                                    @Override
                                    public int getCode() {
                                        return resultCode;
                                    }

                                    @Override
                                    public Uri getData() {
                                        return uri;
                                    }

                                    @Override
                                    public Request getRequest() {
                                        return request;
                                    }

                                    @Override
                                    public Intent getIntent() {
                                        return intent;
                                    }

                                    @Override
                                    public Type getType() {
                                        return Type.Others;
                                    }

                                    @Override
                                    public Feedback getFeedback() {
                                        return Feedback.Success;
                                    }
                                };
                            }
                        else result = new ResultDenied() {
                            @Override
                            public String getCause() {
                                return intent.getAction();
                            }

                            @Override
                            public int getCode() {
                                return resultCode;
                            }

                            @Override
                            public Intent getIntent() {
                                return intent;
                            }

                            @Override
                            public Request getRequest() {
                                return request;
                            }

                            @Override
                            public Type getType() {
                                switch (requestCode) {
                                    case AUDIO:
                                        return Type.Audio;
                                    case IMAGE:
                                        return Type.Image;
                                    case VIDEO:
                                        return Type.Video;
                                    default:
                                        return Type.Others;
                                }
                            }

                            @Override
                            public Feedback getFeedback() {
                                return Feedback.Denied;
                            }
                        };
                        if (request.getListener() != null) request.getListener().onActivityResult(result);
                    }
                }
        }

        public Requests getRequests() {
            return new Requests(requests);
        }

        public Results getResults() {
            return new Results(results);
        }
    }

}

