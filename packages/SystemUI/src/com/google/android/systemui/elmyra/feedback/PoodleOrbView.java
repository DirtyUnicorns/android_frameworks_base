// package com.google.android.systemui.elmyra.feedback;

// import android.animation.Animator;
// import android.animation.Animator.AnimatorListener;
// import android.animation.Keyframe;
// import android.animation.ObjectAnimator;
// import android.animation.PropertyValuesHolder;
// import android.animation.ValueAnimator;
// import android.content.Context;
// import android.graphics.Path;
// import android.util.AttributeSet;
// import android.util.TypedValue;
// import android.view.View;
// import android.view.animation.OvershootInterpolator;
// import android.widget.FrameLayout;
// import com.android.systemui.R;
// import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Iterator;

// public class PoodleOrbView extends FrameLayout implements AnimatorListener, FeedbackEffect {
//     private ArrayList<ValueAnimator> mAnimations = new ArrayList();
//     private View mBackground;
//     private View mBlue;
//     private int mFeedbackHeight;
//     private View mGreen;
//     private View mRed;
//     private int mState = 0;
//     private View mYellow;

//     public PoodleOrbView(Context context) {
//         super(context);
//     }

//     public PoodleOrbView(Context context, AttributeSet attributeSet) {
//         super(context, attributeSet);
//     }

//     public PoodleOrbView(Context context, AttributeSet attributeSet, int i) {
//         super(context, attributeSet, i);
//     }

//     public PoodleOrbView(Context context, AttributeSet attributeSet, int i, int i2) {
//         super(context, attributeSet, i, i2);
//     }

//     private Keyframe[][] approximatePath(Path path, float f, float f2) {
//         float[] approximate = path.approximate(0.5f);
//         Keyframe[] keyframeArr = new Keyframe[(approximate.length / 3)];
//         Keyframe[] keyframeArr2 = new Keyframe[(approximate.length / 3)];
//         int i = 0;
//         int i2 = 0;
//         while (i2 < approximate.length) {
//             int i3 = i2 + 1;
//             float f3 = (approximate[i2] * (f2 - f)) + f;
//             int i4 = i3 + 1;
//             keyframeArr[i] = Keyframe.ofFloat(f3, approximate[i3]);
//             keyframeArr2[i] = Keyframe.ofFloat(f3, approximate[i4]);
//             i++;
//             i2 = i4 + 1;
//         }
//         return new Keyframe[][]{keyframeArr, keyframeArr2};
//     }

//     private ObjectAnimator[] createBackgroundAnimator(View view) {
//         int i = 0;
//         new Keyframe[]{Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.375f, 1.2f), Keyframe.ofFloat(0.75f, 1.2f), Keyframe.ofFloat(0.95f, 0.2f), Keyframe.ofFloat(1.0f, 0.0f)}[1].setInterpolator(new OvershootInterpolator());
//         Keyframe ofFloat = Keyframe.ofFloat(0.0f, view.getTranslationY());
//         Keyframe ofFloat2 = Keyframe.ofFloat(0.375f, m10px(27.5f));
//         Keyframe ofFloat3 = Keyframe.ofFloat(0.75f, m10px(27.5f));
//         Keyframe ofFloat4 = Keyframe.ofFloat(0.95f, m10px(21.75f));
//         r6 = new ObjectAnimator[3];
//         r6[0] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_X, r1)});
//         r6[1] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_Y, r1)});
//         PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[1];
//         propertyValuesHolderArr[0] = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, new Keyframe[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
//         r6[2] = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderArr);
//         int length = r6.length;
//         while (i < length) {
//             r6[i].setDuration(1000);
//             i++;
//         }
//         return r6;
//     }

//     private ObjectAnimator[] createDotAnimator(View view, float f, Path path) {
//         r3 = new Keyframe[4];
//         r3[0] = Keyframe.ofFloat(0.0f, view.getScaleX());
//         r3[1] = Keyframe.ofFloat(0.75f, view.getScaleX());
//         r3[2] = Keyframe.ofFloat(0.95f, 0.3f);
//         r3[3] = Keyframe.ofFloat(1.0f, 0.0f);
//         Keyframe ofFloat = Keyframe.ofFloat(0.0f, 1.0f);
//         Keyframe ofFloat2 = Keyframe.ofFloat(0.75f, 1.0f);
//         Keyframe ofFloat3 = Keyframe.ofFloat(0.95f, 0.25f);
//         Keyframe ofFloat4 = Keyframe.ofFloat(1.0f, 0.0f);
//         Keyframe[][] approximatePath = approximatePath(path, 0.75f, 1.0f);
//         Object obj = new Keyframe[(approximatePath[0].length + 2)];
//         obj[0] = Keyframe.ofFloat(0.0f, view.getTranslationX());
//         obj[1] = Keyframe.ofFloat(0.75f, view.getTranslationX());
//         System.arraycopy(approximatePath[0], 0, obj, 2, approximatePath[0].length);
//         Object obj2 = new Keyframe[(approximatePath[1].length + 3)];
//         obj2[0] = Keyframe.ofFloat(0.0f, view.getTranslationY());
//         obj2[1] = Keyframe.ofFloat(f, view.getTranslationY());
//         obj2[2] = Keyframe.ofFloat(0.75f, view.getTranslationY() - ((float) this.mFeedbackHeight));
//         System.arraycopy(approximatePath[1], 0, obj2, 3, approximatePath[1].length);
//         obj2[2].setInterpolator(new OvershootInterpolator());
//         r8 = new ObjectAnimator[5];
//         r8[0] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_X, r3)});
//         r8[1] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.SCALE_Y, r3)});
//         r8[2] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, obj)});
//         r8[3] = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y, obj2)});
//         PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[1];
//         propertyValuesHolderArr[0] = PropertyValuesHolder.ofKeyframe(View.ALPHA, new Keyframe[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
//         r8[4] = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderArr);
//         for (ObjectAnimator duration : r8) {
//             duration.setDuration(1000);
//         }
//         return r8;
//     }

//     /* renamed from: px */
//     private float m10px(float f) {
//         return TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
//     }

//     public void onAnimationCancel(Animator animator) {
//     }

//     public void onAnimationEnd(Animator animator) {
//         this.mState = 0;
//         onProgress(0.0f, 0);
//     }

//     public void onAnimationRepeat(Animator animator) {
//     }

//     public void onAnimationStart(Animator animator) {
//     }

//     protected void onFinishInflate() {
//         super.onFinishInflate();
//         this.mBackground = findViewById(R.id.elmyra_feedback_background);
//         this.mBlue = findViewById(R.id.elmyra_feedback_blue);
//         this.mGreen = findViewById(R.id.elmyra_feedback_green);
//         this.mRed = findViewById(R.id.elmyra_feedback_red);
//         this.mYellow = findViewById(R.id.elmyra_feedback_yellow);
//         this.mFeedbackHeight = getResources().getDimensionPixelSize(R.dimen.opa_elmyra_orb_height);
//         this.mBackground.setScaleX(0.0f);
//         this.mBackground.setScaleY(0.0f);
//         this.mBlue.setTranslationY(this.mBlue.getTranslationY() + ((float) this.mFeedbackHeight));
//         this.mGreen.setTranslationY(this.mGreen.getTranslationY() + ((float) this.mFeedbackHeight));
//         this.mRed.setTranslationY(this.mRed.getTranslationY() + ((float) this.mFeedbackHeight));
//         this.mYellow.setTranslationY(this.mYellow.getTranslationY() + ((float) this.mFeedbackHeight));
//         this.mAnimations.addAll(Arrays.asList(createBackgroundAnimator(this.mBackground)));
//         ((ValueAnimator) this.mAnimations.get(0)).addListener(this);
//         Path path = new Path();
//         path.moveTo(this.mBlue.getTranslationX(), this.mBlue.getTranslationY() - ((float) this.mFeedbackHeight));
//         path.cubicTo(m10px(-32.5f), m10px(-27.5f), m10px(15.0f), m10px(-33.75f), m10px(-2.5f), m10px(-20.0f));
//         this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mBlue, 0.0f, path)));
//         path = new Path();
//         path.moveTo(this.mRed.getTranslationX(), this.mRed.getTranslationY() - ((float) this.mFeedbackHeight));
//         path.cubicTo(m10px(-25.0f), m10px(-17.5f), m10px(-20.0f), m10px(-27.5f), m10px(2.5f), m10px(-20.0f));
//         this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mRed, 0.05f, path)));
//         path = new Path();
//         path.moveTo(this.mYellow.getTranslationX(), this.mYellow.getTranslationY() - ((float) this.mFeedbackHeight));
//         path.cubicTo(m10px(21.25f), m10px(-33.75f), m10px(15.0f), m10px(-27.5f), m10px(0.0f), m10px(-20.0f));
//         this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mYellow, 0.1f, path)));
//         path = new Path();
//         path.moveTo(this.mGreen.getTranslationX(), this.mGreen.getTranslationY() - ((float) this.mFeedbackHeight));
//         path.cubicTo(m10px(-27.5f), m10px(-20.0f), m10px(35.0f), m10px(-30.0f), m10px(0.0f), m10px(-20.0f));
//         this.mAnimations.addAll(Arrays.asList(createDotAnimator(this.mGreen, 0.2f, path)));
//     }

//     public void onProgress(float f, int i) {
//         if (this.mState != 3) {
//             Iterator it = this.mAnimations.iterator();
//             while (it.hasNext()) {
//                 ValueAnimator valueAnimator = (ValueAnimator) it.next();
//                 valueAnimator.cancel();
//                 valueAnimator.setCurrentFraction((0.75f * f) + 0.0f);
//             }
//             if (f == 0.0f) {
//                 this.mState = 0;
//             } else if (f == 1.0f) {
//                 this.mState = 2;
//             } else {
//                 this.mState = 1;
//             }
//         }
//     }

//     public void onRelease() {
//         if (this.mState == 2 || this.mState == 1) {
//             Iterator it = this.mAnimations.iterator();
//             while (it.hasNext()) {
//                 ((ValueAnimator) it.next()).reverse();
//             }
//             this.mState = 0;
//         }
//     }

//     public void onResolve(DetectionProperties detectionProperties) {
//         Iterator it = this.mAnimations.iterator();
//         while (it.hasNext()) {
//             ((ValueAnimator) it.next()).start();
//         }
//         this.mState = 3;
//     }
// }
