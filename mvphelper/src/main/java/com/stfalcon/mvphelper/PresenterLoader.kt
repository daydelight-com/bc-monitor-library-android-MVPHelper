/*
 * This file has been modified by Day Delight Inc. in 2020.
 * Original: https://github.com/stfalcon-studio/MVPHelper (Copyright 2017 stfalcon.com)
 * Changes:
 *   - Migrated from the Android Support Library to AndroidX (androidx.loader).
 *
 * Licensed under the Apache License, Version 2.0. See the LICENSE file in the root of this repository.
 */

package com.stfalcon.mvphelper

import android.content.Context
import androidx.loader.content.Loader
import javax.inject.Inject

/*
 * Created by troy379 on 07.06.17.
 */
class PresenterLoader<PRESENTER : IPresenter<*>>
@Inject constructor(
        context: Context,
        var presenter: PRESENTER?)
    : Loader<PRESENTER>(context) {

    override fun onStartLoading() {
        deliverResult(presenter)
    }

    override fun onReset() {
        presenter?.onDestroy()
        presenter = null
    }
}