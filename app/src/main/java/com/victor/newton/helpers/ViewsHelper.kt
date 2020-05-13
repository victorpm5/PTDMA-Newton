package com.victor.newton.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import android.view.View

class ViewsHelper(private val context: Context) {

    fun showView(view: View){
        view.visibility = View.VISIBLE
        view.alpha = 0.0f;

        //Mostrem la vista
        view.animate()
            .alpha(1.0f)
            .setDuration(500)
            .setListener(null)
    }

    fun hideView(view: View){
        val handler = Handler()
        handler.postDelayed({
            view.animate()
                .alpha(0.0f)
                .setDuration(1000)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
        }, 5000)
    }
}