package com.gongjiebin.latticeview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动换行
 */
public class AutoLineLayout<T extends KVBean> extends LatticeView {

    public static final int TYPE_NOT = 0;
    /**
     * 单选类型
     */
    public static final int TYPE_RADIO = 1;

    /**
     * 复选类型
     */
    public static final int TYPE_GROUP = 2;

    /**
     * 存放已经绘制出来的views
     */
    public List<T> views;

    public AutoEditParams editParams;
    @Override
    public void loadView() {
        // 初始化调用
        textViews = new ArrayList<>();
    }


    public void setEditParams(AutoEditParams editParams) {
        this.editParams = editParams;
    }

    public AutoLineLayout(Context context) {
        super(context);
    }

    public AutoLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setViews(List<T> views) {
        this.views = views;
    }

    public List<T> getViews() {
        return views;
    }


    @Override
    public void removeViews() {
        super.removeViews();
    }


    /**
     * 得到当前选中的视图
     *
     * @return
     */
    public List<T> getSelKvList() {
        List<T> list = new ArrayList<>();
        for (T v : views) {
            if (v.isSel) {
                list.add(v);
            }
        }
        return list;
    }


    @Override
    public boolean startView() {
        if (views == null || views.size() == 0) return false;
        if (editParams == null) return false;
        if (editParams.width == 0) return false;
        textViews.clear();
        // 创建view
        createView(getViews(), 0);
        return true;
    }


    /**
     * 采用递归创建行数
     */
    public void createView(List<T> views, int position) {
        if (views == null || views.size() == 0) return;
        LinearLayout linearLayout = new LinearLayout(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL); //
        //linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        linearLayout.setPadding(0, 10, 0, 10);
        linearLayout.setLayoutParams(layoutParams);
        List<T> viewList = new ArrayList<>();
        viewList.addAll(views);

        List<T> delStr = new ArrayList<>();
        int views_w = 0; //
        for (int i = 0; i < viewList.size(); i++) {
            final T view = viewList.get(i);
            if (view != null) {
                TextView textView = new TextView(mContext);
                LayoutParams tr = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                tr.setMargins(dip2px(mContext, editParams.margin_lr), 0, dip2px(mContext, editParams.margin_lr), 0);
                textView.setLayoutParams(tr);
                textView.setPadding(dip2px(mContext, 10), dip2px(mContext, 5), dip2px(mContext, 10), dip2px(mContext, 5));
                textView.setText(viewList.get(i).getValue());

                if (!view.isSel) {
                    if (editParams.textSize != 0) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSize);
                    }

                    if (editParams.getTextColor() != 0) {
                        textView.setTextColor(mContext.getResources().getColor(editParams.getTextColor()));
                    }
                    if (editParams.auto_bg_color != 0) {
                        textView.setBackgroundResource(editParams.auto_bg_color);
                    }
                } else {
                    if (editParams.textSelectSize != 0)
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSelectSize);
                    if (editParams.textSelectColor != 0)
                        textView.setTextColor(mContext.getResources().getColor(editParams.textSelectColor));
                    if (editParams.select_bg_color != 0)
                        textView.setBackgroundResource(editParams.select_bg_color);
                }

                if (editParams.isEnable && view.itemNotSel) {
                    textView.setTextColor(mContext.getResources().getColor(editParams.enableColor));
                }

                if (editParams.isTextBold) {
                    // 加粗显示
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                textView.setOnClickListener(getOnItemClickListener(view));

                float textWidth = textView.getPaint().measureText(viewList.get(i).getValue()) + dip2px(mContext, 35) + (dip2px(mContext, editParams.margin_lr) * 2);
                views_w += textWidth;
                if (views_w > editParams.width) {
                    if (i == 0) {
                        delStr.add(viewList.get(i));
                        textView.setTag(view);
                        textViews.add(textView);
                        linearLayout.addView(textView, i);
                    } else {
                        break;
                    }
                } else {
                    delStr.add(viewList.get(i));
                    textView.setTag(view);
                    textViews.add(textView);
                    linearLayout.addView(textView, i);
                }
            }
        }

        for (int i = 0; i < delStr.size(); i++) {
            viewList.remove(delStr.get(i));
        }
        ll_lattice.addView(linearLayout, position);

        this.createView(viewList, position + 1); // 递归调用
    }


    /**
     * bind item onclick
     */
    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T extends KVBean> {
        void onClickItem(View view, T t);
    }

    public LatticeView.OnItemClickListener getOnItemClickListener(final T kv) {
        LatticeView.OnItemClickListener onItemClickListener = new LatticeView.OnItemClickListener() {
            @Override
            public void onClick(View v) {
                if (editParams != null) {
                    switch (editParams.sel_type) {
                        case TYPE_RADIO:
                            radioSel(kv);
                            break;
                        case TYPE_GROUP:
                            groupSel(kv);
                            break;
                        default:{
                            groupNotSel(kv);
                        }
                    }
                }

                if (AutoLineLayout.this.onItemClickListener != null) {
                    AutoLineLayout.this.onItemClickListener.onClickItem(v, kv);
                }
            }
        };

        return onItemClickListener;
    }


    /**
     * 多选
     */
    public void groupNotSel(T kv) {
        if (textViews == null) {
            return;
        }

        for (TextView v : textViews) {
            if (v.getTag() == null) {
                continue;
            }
            if (editParams.textSelectColor != 0)
                v.setTextColor(mContext.getResources().getColor(editParams.textSelectColor));
            if (editParams.textSelectSize != 0)
                v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSelectSize);
            v.setBackgroundResource(editParams.select_bg_color);
        }
    }

    /**
     * 多选
     */
    public void groupSel(T kv) {
        if (textViews == null) {
            return;
        }

        for (TextView v : textViews) {
            if (v.getTag() == null) {
                continue;
            }
            T kvTag = (T) v.getTag();
            if (kvTag != kv) {
                continue;
            }

            if (kvTag.isSel) {
                // 其它的为未选中
                kvTag.isSel = false;
                if (editParams.textColor != 0)
                    v.setTextColor(mContext.getResources().getColor(editParams.textColor));
                if (editParams.textSize != 0)
                    v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSize);
                v.setBackgroundResource(editParams.auto_bg_color);
                continue;
            }

            // 如果是同一个对象。选中
            kvTag.isSel = true;
            if (editParams.textSelectColor != 0)
                v.setTextColor(mContext.getResources().getColor(editParams.textSelectColor));
            if (editParams.textSelectSize != 0)
                v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSelectSize);
            v.setBackgroundResource(editParams.select_bg_color);
        }
    }


    /**
     * 单选
     */
    public void radioSel(T kv) {
        if (textViews == null) {
            return;
        }
        for (TextView v : textViews) {
            if (v.getTag() == null) {
                continue;
            }

            T kvTag = (T) v.getTag();
            if (kvTag == kv) {
                if (kvTag.isSel && !editParams.isCancellation) {
                    kvTag.isSel = false;
                    if (editParams.textColor != 0)
                        v.setTextColor(mContext.getResources().getColor(editParams.textColor));
                    if (editParams.textSize != 0)
                        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSize);
                    // 其它的为未选中
                    v.setBackgroundResource(editParams.auto_bg_color);
                    if (editParams.isEnable && kvTag.itemNotSel) {
                        v.setTextColor(mContext.getResources().getColor(editParams.enableColor));
                    }
                    continue;
                }
                kvTag.isSel = true;
                if (editParams.textSelectColor != 0)
                    v.setTextColor(mContext.getResources().getColor(editParams.textSelectColor));
                if (editParams.textSelectSize != 0)
                    v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSelectSize);
                // 如果是同一个对象。选中
                v.setBackgroundResource(editParams.select_bg_color);

                if (editParams.isEnable && kvTag.itemNotSel) {
                    v.setTextColor(mContext.getResources().getColor(editParams.enableColor));
                }
                continue;
            }

            kvTag.isSel = false;
            if (editParams.textColor != 0)
                v.setTextColor(mContext.getResources().getColor(editParams.textColor));
            if (editParams.textSize != 0)
                v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, editParams.textSize);
            // 其它的为未选中
            v.setBackgroundResource(editParams.auto_bg_color);

            if (editParams.isEnable && kvTag.itemNotSel) {
                v.setTextColor(mContext.getResources().getColor(editParams.enableColor));
            }
        }
    }


    /**
     * @author gongjiebin
     */
    public static class AutoEditParams extends ImageTextParams {

        public int auto_bg_color;
        /**
         * 你可以设定你喜欢的删除图片，不设置的话默认使用本系统的
         */
        public int delImg;

        /**
         * 删除图片是不是放在左边, true 则显示在左上角
         */
        public boolean IsDelImgLeft;

        /**
         * 是否显示删除图片,默认显示、
         * <p>
         * 如果isShowDelImg = false, 删除功能将会隐藏，此布局的功能将会与AutoLineLayout保持一致
         * 那么建议使用AutoLineLayout
         * <p>
         * 如果isShowDelImg= true delImg与IsDelImgLeft才会生效。
         */
        public boolean isShowDelImg = true;

        /**
         * 选中之后应该展示的背景
         */
        public int select_bg_color;


        /**
         * 取值 0 ｜ 1 ｜ 2
         * <p>
         * 0 =代表不需要有选中状态。设置select_bg_color将不会有任何作用
         * 1 =代表单选， 选中一个之后其它的不选中
         * 2 =代表多选，
         */
        public int sel_type = 0;

        /**
         * 本控件的宽度，或者屏幕的宽度。或者设置一个合适的值(必须设置)
         */
        public int width = 0;

        public int margin_lr = 2;
        // 单选时存在使用，如果为true, 那就不会取消选中的item
        public boolean isCancellation;
        // 是否启用不可选中功能 (不可选中状态会)
        public boolean isEnable;
        public int enableColor ;
    }

}
