package com.dicoding.picodiploma.loginwithanimation.view.listStory

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.AddStory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.MapsStory.MapsStoryActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityListStoryBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.example.githubuser2.settings.DarkPreferences
import com.example.githubuser2.settings.DarkViewModel
import com.example.githubuser2.settings.DarkViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DarkMode")

class ListStoryAppActivity : AppCompatActivity() {

    private var _binding: ActivityListStoryBinding? = null
    private val binding get() = _binding

    private lateinit var user: UserModel
    private lateinit var adapter: ListStoryAppAdapter

    private val viewModel: ListStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        user = intent.getParcelableExtra(EXTRA_USER)!!
        adapter = ListStoryAppAdapter()

        setupAdapter()
        locationStory()
        addStoryAction()
        setupSwipeToRefresh()

        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme_activity)

        val pref = DarkPreferences.getInstance(dataStore)

        val mainViewModel = ViewModelProvider(this, DarkViewModelFactory(pref))[DarkViewModel::class.java]
        mainViewModel.getThemeSettings().observe(this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAdapter(){
        adapter = ListStoryAppAdapter()
        binding?.rvPhotos?.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateStoryAdapter { adapter.retry() },
            header = LoadingStateStoryAdapter { adapter.retry() }
        )
        binding?.rvPhotos?.layoutManager = LinearLayoutManager(this)
        binding?.rvPhotos?.setHasFixedSize(true)

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding?.tvInfo?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (adapter.itemCount < 1) binding?.tvInfo?.root?.visibility = View.VISIBLE
            else binding?.tvInfo?.root?.visibility = View.GONE
        }

        viewModel.getStory(user.token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    // update data when swipe
    private fun setupSwipeToRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener { adapter.refresh() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun locationStory() {
        binding?.fabNewLocationStory?.setOnClickListener {
            val moveToMapStory = Intent(this, MapsStoryActivity::class.java)
            moveToMapStory.putExtra(AddStoryActivity.EXTRA_USER, user)
            startActivity(moveToMapStory)
        }
    }

    private fun addStoryAction(){
        binding?.fabNewStory?.setOnClickListener {
            val moveToAddStoryActivity = Intent(this, AddStoryActivity::class.java)
            moveToAddStoryActivity.putExtra(AddStoryActivity.EXTRA_USER, user)
            startActivity(moveToAddStoryActivity)
            finish()
        }
    }

    companion object {
        const val EXTRA_USER = "user"
    }
}