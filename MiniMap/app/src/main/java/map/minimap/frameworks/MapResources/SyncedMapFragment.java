package map.minimap.frameworks.mapResources;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Matthew on 4/5/2015.
 */
public class SyncedMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    private MapViewWrapper mWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mOriginalContentView = super.onCreateView(inflater, container, savedInstanceState);
        mWrapper = new MapViewWrapper(getActivity());
        mWrapper.addView(super.onCreateView(inflater, container, savedInstanceState));
        return mWrapper;
    }

    public void animateMarkerToGB(Marker marker, LatLng finalPosition, LatLngInterpolator latLngInterpolator,
                                  long duration)
    {
        if(mWrapper == null){
            throw new IllegalStateException("MapFragment view not yet created.");
        }
        mWrapper.animateMarkerToGB(marker, finalPosition, latLngInterpolator, duration);
    }
    @Override
    public View getView() {
        return mOriginalContentView;
    }
    public void setOnDragListener(MapViewWrapper.OnDragListener onDragListener) {
        mWrapper.setOnDragListener(onDragListener);
    }

    public static class MapViewWrapper extends FrameLayout
    {
        private HashSet<MarkerAnimation> mAnimations = new HashSet<MarkerAnimation>();
        private OnDragListener mOnDragListener;

        public MapViewWrapper(Context context)
        {
            super(context);
            setWillNotDraw(false);
        }
        public interface OnDragListener {
            public void onDrag(MotionEvent motionEvent);
        }
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (mOnDragListener != null) {
                mOnDragListener.onDrag(ev);
            }
            return super.dispatchTouchEvent(ev);
        }

        public void setOnDragListener(OnDragListener mOnDragListener) {
            this.mOnDragListener = mOnDragListener;
        }

        public void animateMarkerToGB(Marker marker, LatLng finalPosition, LatLngInterpolator latLngInterpolator,
                                      long duration)
        {
            mAnimations.add(new MarkerAnimation(marker, finalPosition, latLngInterpolator, duration));
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            boolean shouldPost = false;

            Iterator<MarkerAnimation> iterator = mAnimations.iterator();
            while(iterator.hasNext()){
                MarkerAnimation markerAnimation = iterator.next();
                if(markerAnimation.animate()){
                    shouldPost = true;
                }
                else{
                    iterator.remove();
                }
            }

            if(shouldPost){
                postInvalidateOnAnimation();
            }
        }

        @SuppressLint("NewApi")
        public void postInvalidateOnAnimation()
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                super.postInvalidateOnAnimation();
            }
            else{
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }


        private static class MarkerAnimation
        {
            final static Interpolator sInterpolator = new AccelerateDecelerateInterpolator();

            private final Marker mMarker;
            private final LatLngInterpolator mLatLngInterpolator;
            private final LatLng mStartPosition;
            private final LatLng mFinalPosition;
            private final long mDuration;
            private final long mStartTime;


            public MarkerAnimation(Marker marker, LatLng finalPosition, LatLngInterpolator latLngInterpolator,
                                   long duration)
            {
                mMarker = marker;
                mLatLngInterpolator = latLngInterpolator;
                mStartPosition = marker.getPosition();
                mFinalPosition = finalPosition;
                mDuration = duration;
                mStartTime = AnimationUtils.currentAnimationTimeMillis();
            }

            public boolean animate()
            {
                // Calculate progress using interpolator
                long elapsed = AnimationUtils.currentAnimationTimeMillis() - mStartTime;
                float t = elapsed / (float)mDuration;
                float v = sInterpolator.getInterpolation(t);
                mMarker.setPosition(mLatLngInterpolator.interpolate(v, mStartPosition, mFinalPosition));

                // Repeat till progress is complete.
                return (t < 1);
            }

            @Override
            public int hashCode()
            {
                // So we only get one animation for the same marker in our HashSet
                return mMarker.hashCode();
            }

            @Override
            public boolean equals(Object o)
            {
                if(o instanceof Marker){
                    return mMarker.equals(o);
                }
                return super.equals(o);
            }
        }
    }
}
