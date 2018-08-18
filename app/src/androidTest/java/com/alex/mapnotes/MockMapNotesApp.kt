package com.alex.mapnotes

import com.alex.mapnotes.data.provider.LocationProvider
import com.alex.mapnotes.data.repository.NotesRepository
import com.alex.mapnotes.data.repository.UserRepository
import org.koin.KoinContext
import org.koin.android.ext.android.startKoin
import org.koin.standalone.StandAloneContext

class MockMapNotesApp : MapNotesApp() {

    override fun initDI() {
        startKoin(this, listOf(testAppModule))
    }

    companion object {
        val mockedUserRepository: UserRepository
            get() {
                return (StandAloneContext.koinContext as KoinContext).get { emptyMap() }
            }
        val mockedNotesRepository: NotesRepository
            get() {
                return (StandAloneContext.koinContext as KoinContext).get { emptyMap() }
            }
        val mockedLocationProvider: LocationProvider
            get() {
                return (StandAloneContext.koinContext as KoinContext).get { emptyMap() }
            }
    }
}