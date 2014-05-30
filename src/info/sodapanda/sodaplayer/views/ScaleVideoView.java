package info.sodapanda.sodaplayer.views;

import android.content.Context;
import android.util.Log;
import android.widget.VideoView;

public class ScaleVideoView extends VideoView {
    private int mAspectRatioWidth = 4;
    private int mAspectRatioHeight = 3;

	public ScaleVideoView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;

        int finalWidth, finalHeight;

        if (calculatedHeight > originalHeight)
        {
            finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
            finalHeight = originalHeight;
        }
        else
        {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }
        Log.d("onLTest", "originalHeight:" + originalHeight + "finalHeight:" + finalHeight);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
	}

}
