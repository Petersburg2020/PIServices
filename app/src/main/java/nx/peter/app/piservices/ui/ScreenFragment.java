package nx.peter.app.piservices.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import nx.peter.app.piservices.BaseScreen;

public class ScreenFragment extends Fragment {
    @LayoutRes
    private final int layoutRes;
    private final String name;
    private ScreenFragment previous, next;
    private OnBackPressedListener onBackPressedListener;



    public ScreenFragment(int layoutRes, String name) {
        this(layoutRes, name, null);
    }

    public ScreenFragment(@LayoutRes int layoutRes, @NonNull String name, @Nullable ScreenFragment previous) {
        this(layoutRes, name, previous, null);
    }

    public ScreenFragment(@LayoutRes int layoutRes, @NonNull String name, @Nullable ScreenFragment previous, @Nullable ScreenFragment next) {
        this(layoutRes, name, previous, next, screen -> screen.setFragment(screen.getScreenStack().getPreviousScreen()));
    }

    public ScreenFragment(@LayoutRes int layoutRes, @NonNull String name, @Nullable ScreenFragment previous, @Nullable ScreenFragment next, @Nullable OnBackPressedListener listener) {
        this.layoutRes = layoutRes;
        this.name = name;
        this.previous = previous;
        this.next = next;
        onBackPressedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutRes, container, false);
    }

    public <S extends BaseScreen> BaseScreen getScreen() {
        return (S) requireActivity();
    }

    public boolean hasNextScreen() {
        return next != null;
    }

    public boolean hasPreviousScreen() {
        return previous != null;
    }

    public ScreenFragment getPrevious() {
        return previous;
    }

    public void setPrevious(@Nullable ScreenFragment previous) {
        this.previous = previous;
    }

    public ScreenFragment getNext() {
        return next;
    }

    public OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public void setNext(@Nullable ScreenFragment next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public boolean equals(@NonNull ScreenFragment another) {
        return equals(another.name);
    }

    public boolean equals(@NonNull CharSequence name) {
        return this.name.contentEquals(name);
    }


    public interface OnBackPressedListener {
        void onBackPressed(@NonNull BaseScreen screen);
    }

}
