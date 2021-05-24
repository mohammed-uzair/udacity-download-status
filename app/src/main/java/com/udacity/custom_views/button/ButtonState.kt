package com.udacity.custom_views.button

sealed class ButtonState {
    object Idle : ButtonState()
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}