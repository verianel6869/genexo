// Generated code from Butter Knife. Do not modify!
package com.mygdx.game.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class StoresRecyclerViewAdapter$StoreViewHolder$$ViewBinder<T extends com.mygdx.game.adapter.StoresRecyclerViewAdapter.StoreViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492988, "field 'mStoreName'");
    target.mStoreName = finder.castView(view, 2131492988, "field 'mStoreName'");
    view = finder.findOptionalView(source, 2131492989, null);
    target.mSecondLine = finder.castView(view, 2131492989, "field 'mSecondLine'");
  }

  @Override public void unbind(T target) {
    target.mStoreName = null;
    target.mSecondLine = null;
  }
}
