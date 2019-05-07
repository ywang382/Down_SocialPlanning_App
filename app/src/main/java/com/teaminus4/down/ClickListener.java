package com.teaminus4.down;

/**
 * ClickListener allows us to reference our View Holder by a
 * a common name
 */
public interface ClickListener {

    void onPositionClicked(int position, boolean accepted);
}
