package nx.peter.app.piservices.ui.gallery;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import nx.peter.app.piservices.R;
import nx.peter.app.piservices.databinding.FragmentGalleryBinding;
import nx.peter.app.piservices.ui.ScreenFragment;

public class GalleryFragment extends ScreenFragment {
    public static final String NAME = "Gallery";
    private FragmentGalleryBinding binding;

    public GalleryFragment() {
        this(null);
    }

    public GalleryFragment(@Nullable ScreenFragment previous) {
        super(R.layout.fragment_gallery, NAME, previous);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.bind(view);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), binding.textGallery::setText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}