package com.alex.mapnotes.add

import com.alex.mapnotes.base.MvpPresenter
import com.alex.mapnotes.data.formatter.LocationFormatter
import com.alex.mapnotes.data.provider.LocationProvider
import com.alex.mapnotes.data.repository.AuthRepository
import com.alex.mapnotes.data.repository.NotesRepository
import com.alex.mapnotes.model.Note

class AddNotePresenter(private val locationProvider: LocationProvider,
                       private val locationFormatter: LocationFormatter,
                       private val authRepository: AuthRepository,
                       private val notesRepository: NotesRepository) : MvpPresenter<AddNoteView>, AddNoteMvpPresenter {
    private var view: AddNoteView? = null

    override fun onAttach(view: AddNoteView?) {
        this.view = view
        locationProvider.startLocationUpdates()
    }

    override fun getCurrentLocation() {
        locationProvider.addUpdatableLocationListener { location ->
            view?.displayCurrentLocation(locationFormatter.format(location))
        }
    }

    override fun addNote(text: String) {
        locationProvider.addSingleLocationListener {
            val uid = authRepository.getUser()?.uid!!
            val note = Note(it.latitude, it.longitude, text, uid)
            notesRepository.addNote(note)
            view?.clearNoteText()
        }
    }

    override fun onDetach() {
        locationProvider.stopLocationUpdates()
        this.view = null
    }
}