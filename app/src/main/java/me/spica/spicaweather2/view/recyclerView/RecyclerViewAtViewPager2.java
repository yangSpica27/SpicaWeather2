package me.spica.spicaweather2.view.recyclerView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ClassName RecyclerViewAtViewPager2
 * @Author Spica2 7
 * @Date 2023/11/21 17:13
 */
public class RecyclerViewAtViewPager2 extends RecyclerView {

  public RecyclerViewAtViewPager2(@NonNull Context context) {
    super(context);
  }

  public RecyclerViewAtViewPager2(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewAtViewPager2(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private int startX = 0;

  private int startY = 0;

  private float lastY = 0;

  private PullDownListener pullDownListener = null;

  public void setPullDownListener(PullDownListener pullDownListener) {
    this.pullDownListener = pullDownListener;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        startX = (int) ev.getX();
        startY = (int) ev.getY();
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_MOVE:
        int endX = (int) ev.getX();
        int endY = (int) ev.getY();
        int disX = Math.abs(endX - startX);
        int disY = Math.abs(endY - startY);
        if (disX > disY) {
          //如果是纵向滑动，告知父布局不进行时间拦截，交由子布局消费，　requestDisallowInterceptTouchEvent(true)
          getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(startX - endX));
        } else {
          getParent().requestDisallowInterceptTouchEvent(canScrollVertically(startX - endX));
        }
        if (!canScrollVertically(-1) && pullDownListener != null) {
          pullDownListener.onPullDown(ev.getY() - lastY);
        } else {
          lastY = ev.getY();
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        getParent().requestDisallowInterceptTouchEvent(false);
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  public interface PullDownListener {
    void onPullDown(float downY);

    void onPullUp(float downY);

  }

}