package nx.peter.app.piservices;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import nx.peter.app.piservices.ui.ScreenFragment;
import nx.peter.app.util.RequestIntent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen extends RequestIntent.RequestActivity {
    protected ScreenStack screenStack;
    protected NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenStack = new ScreenStack();


    }

    public ScreenStack getScreenStack() {
        return screenStack;
    }

    public abstract void setFragment(@NonNull ScreenFragment fragment);

    @Override
    public void onBackPressed() {
        ScreenFragment screen = screenStack.getCurrentScreen();
        if (screen != null && screen.getOnBackPressedListener() != null)
            screen.getOnBackPressedListener().onBackPressed(this);
        else super.onBackPressed();
    }




    public static class ScreenStack {
        private final List<ScreenFragment> screens;
        private ScreenFragment currentScreen;

        public ScreenStack() {
            this(new ArrayList<>());
        }

        public ScreenStack(@NonNull List<ScreenFragment> screens) {
            this.screens = screens;
            currentScreen = null;
        }

        public void addAfter(@NonNull CharSequence name, @NonNull ScreenFragment screen) {
            if (contains(screen)) remove(screen);
            int pos = getPositionOf(name);
            if (pos >= 0 && pos < size() - 1) screens.add(pos + 1, screen);
            else screens.add(pos, screen);
            update();
        }

        public void addBefore(@NonNull CharSequence name, @NonNull ScreenFragment screen) {
            if (contains(screen)) remove(screen);
            int pos = getPositionOf(name);
            if (pos > 0 && pos < size()) screens.add(pos - 1, screen);
            else if (pos == 0) screens.add(pos, screen);
            else screens.add(screen);
            update();
        }

        public void stack(@NonNull ScreenFragment screen) {
            if (contains(screen)) remove(screen);
            screens.add(screen);
            update();
        }

        public void pop() {
            screens.remove(size() -1);
        }

        public boolean remove(@NonNull CharSequence name) {
            return remove(getScreen(name));
        }

        public boolean remove(@NonNull ScreenFragment screen) {
            boolean r = screens.remove(screen);
            if (r) update();
            return r;
        }

        public boolean contains(@NonNull CharSequence name) {
            return getScreen(name) != null;
        }

        public boolean contains(@NonNull ScreenFragment screen) {
            return screens.contains(screen);
        }

        public boolean isEmpty() {
            return screens.isEmpty();
        }

        public boolean isNotEmpty() {
            return !isEmpty();
        }

        public int size() {
            return screens.size();
        }

        public void setCurrentScreen(@Nullable CharSequence currentScreen) {
            this.currentScreen = currentScreen != null ? getScreen(currentScreen) : null;
        }

        public ScreenFragment getScreen(@NonNull CharSequence name) {
            for (ScreenFragment screen : screens)
                if (screen.equals(name))
                    return screen;
            return null;
        }

        public ScreenFragment getScreen(int position) {
            return position >= 0 && position < size() ? screens.get(position) : null;
        }

        public ScreenFragment getNextScreen() {
            return getScreen(getPositionOf(currentScreen) + 1);
        }

        public ScreenFragment getCurrentScreen() {
            return currentScreen != null ? currentScreen : null;
        }

        public ScreenFragment getPreviousScreen() {
            return getScreen(getPositionOf(currentScreen) - 1);
        }

        public int getPositionOf(@NonNull ScreenFragment screen) {
            return getPositionOf(screen.getName());
        }

        public int getPositionOf(@NonNull CharSequence name) {
            for (int p = 0; p < size(); p++)
                if (getScreen(p).equals(name)) return p;
            return -1;
        }

        protected void update() {
            ScreenFragment previous = null;
            for (ScreenFragment screen : screens) {
                screen.setNext(null);
                screen.setPrevious(previous);
                if (previous != null) previous.setNext(screen);
                previous = screen;
            }
        }

    }

}
