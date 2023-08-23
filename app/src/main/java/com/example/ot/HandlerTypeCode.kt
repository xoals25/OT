package com.example.ot

/**
 *  해당 클래스는 핸들러에서 사용되는 코드넘버를 모아둔 곳이다.
 *
 * @author kevin
 * @version 1.0, MeetingRoomHandler에 필요한 코드넘버 추가
 * @see None
 *
 * @version 1.1, 주석 작성에 필요한 CanvasType 코드 넘버 추가
 */
class HandlerTypeCode {
    /*1번~19번까지*/
    object MeetingRoomAcitvityType {
        const val VIEW_CHANGE_MESSAGE = 1
        const val VIEWPAGER_ADD_PAGE_MESSAGE = 2
        const val SCREEN_SHARE_ICON_ON_CHANGE_MESSAGE = 3
        const val SCREEN_SHARE_ICON_OFF_CHANGE_MESSAGE = 4
        const val SCREEN_SHARE_SURFACEVIEW_INIT = 5
        const val USER_LIST_CHANGE_MESSAGE = 6
        const val USER_PROFILE_CHANGE_MESSAGE =7
    }

    /*20번 ~ 29번까지*/
    object CanvasType{
        const val SCREEN_SHARE_RECEIVER_DRAWER_OPEN = 20
        const val SCREEN_SHARE_RECEIVER_DRAWER_CLOSE = 21
        const val DRAW_LINE_INVALIDATE = 22
        const val SCREEN_SHARE_BTN_VISIBLE_GONE = 23
    }

}