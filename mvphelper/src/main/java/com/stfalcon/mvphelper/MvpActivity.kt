/*
 * This file has been modified by Day Delight Inc. in 2020.
 * Original: https://github.com/stfalcon-studio/MVPHelper (Copyright 2017 stfalcon.com)
 * Changes:
 *   - Migrated from the Android Support Library to AndroidX (androidx.loader).
 *   - Replaced the deprecated supportLoaderManager with LoaderManager.getInstance(this).
 *   - Changed the args parameter of onCreateLoader() to nullable (Bundle?).
 *
 * Licensed under the Apache License, Version 2.0. See the LICENSE file in the root of this repository.
 */

package com.stfalcon.mvphelper

import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import dagger.android.support.DaggerAppCompatActivity
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/*
 * Created by troy379 on 07.06.17.
 */
abstract class MvpActivity<PRESENTER : IPresenter<VIEW>, VIEW> : DaggerAppCompatActivity(),
        LoaderManager.LoaderCallbacks<PRESENTER> {

    protected abstract fun getLayoutResId(): Int

    @Inject lateinit var presenterLoader: PresenterLoader<PRESENTER>
    protected var presenter: PRESENTER? = null

    private var firstStart: Boolean = false
    private val needToCallStart = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        parseIntent()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        firstStart = true
        LoaderManager.getInstance(this).initLoader(0, Bundle.EMPTY, this).startLoading()
    }

    override fun onStart() {
        super.onStart()
        if (presenter == null) needToCallStart.set(true)
        else doStart()
    }

    override fun onStop() {
        if (presenter != null) presenter?.onViewDetached()
        super.onStop()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<PRESENTER> = presenterLoader

    override fun onLoadFinished(loader: Loader<PRESENTER>, presenter: PRESENTER) {
        this.presenter = presenter
        if (needToCallStart.compareAndSet(true, false)) {
            doStart()
        }
    }

    override fun onLoaderReset(loader: Loader<PRESENTER>) {
        presenter = null
    }

    open protected fun parseIntent() {
        //override if needed
    }

    @Suppress("UNCHECKED_CAST")
    private fun doStart() {
        presenter?.onViewAttached(this as VIEW, firstStart)
        firstStart = false
    }
}