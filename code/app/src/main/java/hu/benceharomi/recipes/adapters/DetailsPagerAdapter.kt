package hu.benceharomi.recipes.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.benceharomi.recipes.fragments.ShareFragment
import hu.benceharomi.recipes.fragments.DescriptionFragment

class DetailsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> ShareFragment()
        1 -> DescriptionFragment()
        else -> ShareFragment()
    }

    override fun getCount(): Int = NUM_PAGES

    companion object {
        const val NUM_PAGES = 2
    }
}