package com.simsme.mycustomcalendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

enum ButtonState{
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private static final float buttonWidth = 200; //버튼 너비 지정
    private ButtonState buttonsShowedState = ButtonState.GONE;
    private RectF buttonInstance = null; //버튼 객체 초기 지정
    private ItemTouchHelperListener listener;
    private RecyclerView.ViewHolder currenrtItemViewHolder = null;

    public SwipeController(ItemTouchHelperListener listener){
        this.listener=listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int draw_flags= ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe_flags= ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(draw_flags,swipe_flags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemSwipe(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState==ItemTouchHelper.ACTION_STATE_SWIPE)  {
            if (buttonsShowedState!=ButtonState.GONE){
                if (buttonsShowedState==ButtonState.LEFT_VISIBLE) //오른쪽으로 스와이프 했을 때
                    dX=Math.max(dX, buttonWidth);
                if (buttonsShowedState==ButtonState.RIGHT_VISIBLE) //왼쪽으로 스와이프 했을 때
                    dX=Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }else {
                setTouchListener(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
            }
            if (buttonsShowedState== ButtonState.GONE){
                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
            }
        }
        currenrtItemViewHolder=viewHolder;
        drawButtons(c,currenrtItemViewHolder);
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){ //레이아웃이 아닌 클래스에서 직접 버튼 구현
        float buttonWidthWithOutPadding = buttonWidth-10;
        float corners=5;
        View itemView=viewHolder.itemView;
        Paint p=new Paint(); //Paint 객체 p 생성
        buttonInstance=null;

        //오른쪽으로 스와이프 했을 때, 왼쪽 버튼(수정)이 나와야 함
        //rectF 클래스로 버튼 형태 구현
        if (buttonsShowedState==ButtonState.LEFT_VISIBLE){
            RectF leftButton=new RectF(itemView.getLeft()+10, itemView.getTop()+10, itemView.getLeft()+buttonWidthWithOutPadding,
                    itemView.getBottom()-10); //rectF : top, bottom, left, right에 대한 4가지 정보를 가지고 있는 직사각형 클래스 (rect : int, rectF : float)
            p.setColor(Color.parseColor("#64A0FF")); //Paint 객체 컬러 지정, setColor 메서드는 int형 인자를 매개변수로 받기 때문에 string 형태의 색상을 Color 클래스의 parseColor 메서드를 이용해 int형으로 바꿔줌
            c.drawRoundRect(leftButton,corners,corners,p); //drawRoundRect : 원 그리기
            drawtext("수정",c,leftButton,p);
            buttonInstance=leftButton;
        }else if (buttonsShowedState==ButtonState.RIGHT_VISIBLE){ //왼쪽으로 스와이프 했을 때, 오른쪽 버튼(삭제)이 나와야 함
            RectF rightButton=new RectF(itemView.getRight()-buttonWidthWithOutPadding, itemView.getTop()+10,
                    itemView.getRight()-10,itemView.getBottom()-10);
            p.setColor(Color.parseColor("#FF6347"));
            c.drawRoundRect(rightButton,corners,corners,p);
            drawtext("삭제",c,rightButton,p);
            buttonInstance=rightButton;
        }
    }

    private void drawtext(String text, Canvas c, RectF button, Paint p){ //버튼 내에 글씨 삽입
        float textSize=35;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth=p.measureText(text); //measureText : 글자의 너비 리턴
        c.drawText(text,button.centerX()-(textWidth/2),button.centerY()+(textSize/2),p); //Canvas 객체의 drawText : 글자의 구체적인 속성 정의
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack){
            swipeBack=false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                swipeBack=event.getAction()==MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack){
                    if (dX<-buttonWidth)
                        buttonsShowedState=ButtonState.RIGHT_VISIBLE;
                    else if (dX>buttonWidth)
                        buttonsShowedState=ButtonState.LEFT_VISIBLE;

                    if (buttonsShowedState!=ButtonState.GONE){
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemClickable(recyclerView,false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder, final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    setTouchUpListener(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder, final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SwipeController.super.onChildDraw(c,recyclerView,viewHolder,0F,dY,actionState,isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                setItemClickable(recyclerView,true);
                swipeBack=false;
                /*인터페이스??*/
                if (listener!=null && buttonInstance!=null && buttonInstance.contains(event.getX(), event.getY())){
                    if (buttonsShowedState==ButtonState.LEFT_VISIBLE){
                        listener.onLeftClick(viewHolder.getAdapterPosition(),viewHolder);
                    }else if (buttonsShowedState==ButtonState.RIGHT_VISIBLE){
                        listener.onRightClick(viewHolder.getAdapterPosition(),viewHolder);
                    }
                }
                buttonsShowedState=ButtonState.GONE;
                currenrtItemViewHolder=null;
                return false;
            }
        });
    }

    public void setItemClickable(RecyclerView recyclerView, boolean isClickable){
        for (int i=0; i<recyclerView.getChildCount();i++){
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }
}
