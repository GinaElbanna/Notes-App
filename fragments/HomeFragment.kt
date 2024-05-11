package com.zybooks.thenotesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zybooks.thenotesapp.MainActivity
import com.zybooks.thenotesapp.R
import com.zybooks.thenotesapp.SoundEffects
import com.zybooks.thenotesapp.adapter.NoteAdapter
import com.zybooks.thenotesapp.databinding.FragmentHomeBinding
import com.zybooks.thenotesapp.model.Note
import com.zybooks.thenotesapp.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!
    private lateinit var soundEffects: SoundEffects

    private lateinit var notesViewModel : NoteViewModel
    private var noteAdapter = NoteAdapter(mutableListOf())
    private lateinit var selectedNote: Note

    private var selectedNotePosition = RecyclerView.NO_POSITION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
        soundEffects = SoundEffects.getInstance(requireContext())
        soundEffects.playGameOver()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()

        noteAdapter.noteColors = resources.getIntArray(R.array.noteColors)
        soundEffects = SoundEffects.getInstance(requireContext())

        binding.addNoteFab.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
            soundEffects.playTone(true)
        }
    }
    private fun updateUI(note: List<Note>?){

        if (note != null){
            if (note.isNotEmpty()){

                binding.emptyNotesImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            }else{
                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE

            }
        }

    }
    private fun setupHomeRecyclerView(){
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            //layoutManager = GridLayoutManager(context, 2)

            setHasFixedSize(true)
            adapter = noteAdapter
        }
        activity?.let{
            notesViewModel.getAllNotes().observe(viewLifecycleOwner){note->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }
    private fun searchNote(query: String?){
        val searchQuery = "%$query"

        notesViewModel.searchNote(searchQuery).observe(this){list->
            noteAdapter.differ.submitList(list)

        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    override fun onDestroy(){
        super.onDestroy()
        homeBinding = null
        //soundEffects.release()
        soundEffects.playGameOver()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }



}