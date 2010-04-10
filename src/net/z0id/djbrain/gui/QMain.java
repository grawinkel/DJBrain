/*
 *   Copyright (C) 2006 Matthias Grawinkel <matthias@grawinkel.com>  				   
 *																																			   
 *   This program is free software; you can redistribute it and/or modify        
 *   it under the terms of the GNU General Public License as published by  
 *   the Free Software Foundation; either version 2 of the License, or             
 *   (at your option) any later version.                                   
 *                                                                         
 */

package net.z0id.djbrain.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.z0id.djbrain.db.DBConnection;
import net.z0id.djbrain.gui.about.AboutWindow;
import net.z0id.djbrain.gui.brainstorm.BrainStormWidget;
import net.z0id.djbrain.gui.currenttrack.CurrentTrackWidget;
import net.z0id.djbrain.gui.debug.LogWindow;
import net.z0id.djbrain.gui.debug.QDBDumpWidget;
import net.z0id.djbrain.gui.genre.QEditGenres;
import net.z0id.djbrain.gui.help.HelpBrowserWidget;
import net.z0id.djbrain.gui.lastfmbrowser.LastFMBrowser;
import net.z0id.djbrain.gui.playlist.PlaylistListWidget;
import net.z0id.djbrain.gui.playlist.QNewPlaylistWidget;
import net.z0id.djbrain.gui.preferences.PreferencesWindow;
import net.z0id.djbrain.gui.search.SearchWidget;
import net.z0id.djbrain.gui.tracklist.SuggestedTrackList;
import net.z0id.djbrain.gui.tracklist.TabbedTracklist;
import net.z0id.djbrain.gui.tracklist.TrackList;
import net.z0id.djbrain.imexport.MediaImporter;
import net.z0id.djbrain.net.CheckNewestVersionThread;
import net.z0id.djbrain.objects.Track;
import net.z0id.djbrain.properties.DJProperties;

import org.apache.log4j.Logger;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QBoxLayout;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QSplitter;
import com.trolltech.qt.gui.QToolButton;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class QMain extends QMainWindow {

	private Logger logger = Logger.getLogger("QMain");

	private final int DEFAULT_STATUSBAR_TIME = 3000;

	private CurrentTrackWidget currentTrack;;

	private boolean confirmClose = true;

	/**
	 * if DEBUG is true, the debug-menu is show.
	 */
	public static boolean DEBUG = false;

	private SignalHub signalHub;

	/* =============== Actions =============== */
	private QAction newtrackAct;

	private QAction newPlaylistAct;

	private QAction editGenresAct;

	private QAction importTracksFromDiscogsAct;



	private QAction importTracksFromMP3DirAct;

	// private QAction importTracksFromVirtualDJAct;

	private QAction importDBFromXMLAct;

	private QAction dumpAct;

	private QAction showLogAct;
	

	private QAction exitAct;

	private QAction aboutAct;

	private QAction helpAct;


	private QAction showTrackStackAct;

	private QAction showBrainstormAct;

	private QAction showPreferencesAct;
	
	private QAction showLastFMBrowser;
	

	private QAction setSearchFocus;
	
	
	
	

	/* =============== /Actions =============== */

	private QSplitter mainHSplitter;

	private QSplitter rightVSplitter;

	private TabbedTracklist tabbedTracklist;

	private String rsrcPath = "classpath://images";

	private PlaylistListWidget playlistListWidget;

	private SearchWidget searchWidget;

	

	private String WINDOWTITLE = "DJBrain - the Dj Grimoire - ";

	/**
	 * 
	 */
	public QMain() {

		String d = DJProperties.getProperty("Debug");

		if (d.equalsIgnoreCase("true") || d.equalsIgnoreCase("yes")
				|| d.equalsIgnoreCase("1")) {
			DEBUG = true;
		}
		
		setWindowIcon(new QIcon("classpath://images/djbrain_logo.png"));

		signalHub = new SignalHub(this);

		searchWidget = new SearchWidget(signalHub);

		LastFMBrowser.init(signalHub);

		QMenuBar menuBar = new QMenuBar();
		setMenuBar(menuBar);

		createActions();

		createMenus();

		createStatusBar();

		playlistListWidget = new PlaylistListWidget(signalHub);

		QGridLayout leftBoxLayout = new QGridLayout();

		leftBoxLayout.addWidget(playlistListWidget, 0, 0);
		// Qt.AlignTop);
		leftBoxLayout.addWidget(new TrashWidget(this, signalHub), 1, 0);
		// Qt.AlignBottom);
		leftBoxLayout.setMargin(0);
		QWidget foo = new QWidget();
		foo.setLayout(leftBoxLayout);

		currentTrack = new CurrentTrackWidget(signalHub);

		mainHSplitter = new QSplitter(this);
		rightVSplitter = new QSplitter(mainHSplitter);

		mainHSplitter.setChildrenCollapsible(true);
		mainHSplitter.addWidget(foo);
		mainHSplitter.addWidget(rightVSplitter);

		List<Integer> mainHSplitterSizesList = new ArrayList<Integer>();
		mainHSplitterSizesList.add(150);
		mainHSplitterSizesList.add(850);
		mainHSplitter.setOrientation(Qt.Orientation.Horizontal);
		mainHSplitter.setSizes(mainHSplitterSizesList);

		List<Integer> rightVSplitterSizesList = new ArrayList<Integer>();
		rightVSplitterSizesList.add(400);
		rightVSplitterSizesList.add(80);
		rightVSplitter.setOrientation(Qt.Orientation.Vertical);
		rightVSplitter.setSizes(rightVSplitterSizesList);
		// trackList = new TrackList(signalHub);
		tabbedTracklist = new TabbedTracklist(signalHub);
		tabbedTracklist
				.addPlaylist(tr("Library"), PlaylistListWidget.LIBRARYID);

//		suggestionHSplitter.setOrientation(Qt.Orientation.Horizontal);
		// suggestionHSplitter.setSizes()

		

		// rightVSplitter.addWidget(trackList);
		rightVSplitter.addWidget(tabbedTracklist);
		rightVSplitter.addWidget(currentTrack);
//		rightVSplitter.addWidget(suggestionHSplitter);

		rightVSplitter.setStretchFactor(0, 120);
		rightVSplitter.setStretchFactor(1, 0);
		rightVSplitter.setStretchFactor(2, 60);

		List<Integer> suggestionHSplitterSizesList = new ArrayList<Integer>();
		suggestionHSplitterSizesList.add(650);
		suggestionHSplitterSizesList.add(200);
//		suggestionHSplitter.addWidget(suggestedTrackList);
//		suggestionHSplitter.addWidget(suggestionBox);
//		suggestionHSplitter.setSizes(suggestionHSplitterSizesList);
//		suggestionHSplitter.setStretchFactor(0, 65);
//		suggestionHSplitter.setStretchFactor(1, 20);

		QGridLayout mainLayout = new QGridLayout();
		mainLayout.addWidget(getToolBar(), 0, 0);
		mainLayout.addWidget(mainHSplitter, 1, 0);

		// dont stretch the toolbar
		mainLayout.setRowStretch(0, 1);

		// but stretch the content
		mainLayout.setRowStretch(1, 1000);

		QWidget base = new QWidget();
		base.setLayout(mainLayout);
		setCentralWidget(base);
		// setCentralWidget(mainHSplitter);
		// setLayout(mainLayout);

		// readSettings();

		resize(1000, 700);
		setWindowTitle(WINDOWTITLE + DJProperties.getProperty("version"));
		// setWindowOpacity(0.85);
		// currentTrack.document().contentsChanged.connect(this,
		// "documentWasModified()");

		// check djbrain.net/latest.txt for last version.

		String check = DJProperties.getProperty("checknewversion");

		if (check.equalsIgnoreCase("true") || check.equalsIgnoreCase("yes")
				|| check.equals("1")) {
			signalHub.newVersionAvailable.connect(this,
					"signalNewVersionAvailable(String, String)");
			new CheckNewestVersionThread(signalHub).start();
		}
	}

	@SuppressWarnings("unused")
	private void newTrack() {
		QNewTrackWidget.getInstance(signalHub).show();
	}

	@SuppressWarnings("unused")
	private void signalNewVersionAvailable(final String latestUpdate,
			final String versionString) {
		QMessageBox.information(this, tr("New version available!"), tr("on "
				+ latestUpdate + " version " + versionString
				+ " was released.\nGet it on http://www.djbrain.net"));
	}

	@SuppressWarnings("unused")
	private void newPlaylist() {
		QNewPlaylistWidget.getInstance(signalHub).show();
	}

	/**
	 * 
	 */
	// @SuppressWarnings("unused")
	// private void removePlaylist() {
	// QListWidgetItem item = playlistListWidget.currentItem();
	// if (item instanceof PlaylistItem) {
	// PlaylistItem playlistItem = (PlaylistItem) item;
	// int id = playlistItem.getPlaylist().getId();
	//
	// if (id != -1) {
	// DBConnection.getInstance().deletePlaylist(id);
	//
	// signalHub.signalPlaylistsChanged();
	// } else {
	//
	// QMessageBox.critical(this, tr("Error!"),
	// tr("The Library can not be removed"));
	// }
	// }
	//
	// }
	@SuppressWarnings("unused")
	private void dumbDB() {
		new QDBDumpWidget().show();
	}

	@SuppressWarnings("unused")
	private void about() {
		AboutWindow.showWindow();
	}

	private void createActions() {

		setSearchFocus = new QAction(this);
		setSearchFocus.setShortcut(new QKeySequence(tr("Ctrl+f")));
		setSearchFocus.triggered.connect(searchWidget, "setSearchFocus()");
		this.addAction(setSearchFocus);

		newtrackAct = new QAction(new QIcon(rsrcPath + "/track_new.png"),
				tr("New &Track"), this);
		newtrackAct.setShortcut(new QKeySequence(tr("Ctrl+n")));
		newtrackAct.setStatusTip(tr("Create a track"));
		newtrackAct.triggered.connect(this, "newTrack()");

		newPlaylistAct = new QAction(new QIcon(rsrcPath + "/playlist_new.png"),
				tr("New Play&list"), this);
		newPlaylistAct.setShortcut(new QKeySequence(tr("Ctrl+l")));
		newPlaylistAct.setStatusTip(tr("Create a new Playlist"));
		newPlaylistAct.triggered.connect(this, "newPlaylist()");

		editGenresAct = new QAction(new QIcon(rsrcPath + "/edit_genres.png"),
				tr("&Edit genres"), this);
		editGenresAct.setShortcut(new QKeySequence(tr("Ctrl+g")));
		editGenresAct.setStatusTip(tr("edit list of available genres"));
		editGenresAct.triggered.connect(this, "showEditGenresDialog()");

		helpAct = new QAction(new QIcon(rsrcPath + "/help-browser.png"),
				tr("&Help"), this);
		helpAct.setShortcut(new QKeySequence(tr("Ctrl+H")));
		helpAct.setStatusTip(tr("Show Help"));
		helpAct.triggered.connect(this, "showHelpBrowser()");

		dumpAct = new QAction(new QIcon(rsrcPath + "/dumpdb.png"), tr("&Dump"),
				this);
		dumpAct.setShortcut(new QKeySequence(tr("Ctrl+d")));
		dumpAct.setStatusTip(tr("dumpDB"));
		dumpAct.triggered.connect(this, "dumbDB()");

		showLogAct = new QAction(new QIcon(rsrcPath + "/dumpdb.png"),
				tr("Show &Log"), this);

		showLogAct.setStatusTip(tr("Opens the log window"));
		showLogAct.triggered.connect(this, "showLogWindow()");

	

//		importTracksFromXMLAct = new QAction(new QIcon(rsrcPath
//				+ "/djbrain_import.png"), tr("&Import Tracks from  XML file"),
//				this);
//		// importTracksFromXMLAct.setShortcut(new QKeySequence(tr("Ctrl+I")));
//		importTracksFromXMLAct.setStatusTip(tr("Import Tracks from XML File"));
//		importTracksFromXMLAct.triggered.connect(this, "importTracksFromXML()");

		// importTracksFromVirtualDJAct = new QAction(new QIcon(rsrcPath
		// + "/djbrain_import.png"),
		// tr("&Import Tracks from VirtualDJ XML Database"), this);
		// importTracksFromVirtualDJAct
		// .setStatusTip(tr("Import Tracks from VirtualDJ XML Database"));
		// importTracksFromVirtualDJAct.triggered.connect(this,
		// "importTracksFromVirtualDJ()");

		importTracksFromMP3DirAct = new QAction(new QIcon(rsrcPath
				+ "/djbrain_import.png"), tr("Import MP3s from Directory"),
				this);
		importTracksFromMP3DirAct
				.setStatusTip(tr("Import MP3s from Directory"));
		importTracksFromMP3DirAct.triggered.connect(this,
				"importTracksFromMP3Dir()");

		

//		exportXMLAct = new QAction(new QIcon(rsrcPath + "/djbrain_export.png"),
//				tr("&Export Database as XML"), this);
//		exportXMLAct.setShortcut(new QKeySequence(tr("Ctrl+M")));
//		exportXMLAct.setStatusTip(tr("Export as XML"));
//		exportXMLAct.triggered.connect(this, "showExportXMLDialog()");
//
//		exportTracksAsXMLAct = new QAction(new QIcon(rsrcPath
//				+ "/djbrain_export.png"), tr("&Export Tracks as XML"), this);
//		// exportTracksAsXMLAct.setShortcut(new QKeySequence(tr("Ctrl+M")));
//		exportTracksAsXMLAct.setStatusTip(tr("Export Tracks as XML"));
//		exportTracksAsXMLAct.triggered.connect(this,
//				"showExportTracksAsXMLDialog()");

		exitAct = new QAction(new QIcon(rsrcPath + "/system-log-out.png"),
				tr("Exit"), this);
		exitAct.setShortcut(tr("Ctrl+Q"));
		exitAct.setStatusTip(tr("Exit the application"));
		exitAct.triggered.connect(this, "closeWindowWithoutConfirm()");

		showTrackStackAct = new QAction(new QIcon(
				"classpath://images/trackstack.png"), tr("&Show TrackStack"),
				this);
		showTrackStackAct.setShortcut(tr("Ctrl+T"));
		showTrackStackAct.setStatusTip(tr("Show TrackStack Window"));
		showTrackStackAct.triggered.connect(this, "showTrackStack()");

		showBrainstormAct = new QAction(new QIcon(
				"classpath://images/brainstorm.png"), tr("&Show Brainstorm"),
				this);
		showBrainstormAct.setShortcut(tr("Ctrl+B"));
		showBrainstormAct.setStatusTip(tr("Show Brainstorm Window"));
		showBrainstormAct.triggered.connect(this, "showBrainstorm()");

		showPreferencesAct = new QAction(new QIcon(
				"classpath://images/preferences.png"), tr("&Preferences"), this);
		// showPreferencesAct.setShortcut(tr("Ctrl+P"));
		showPreferencesAct.setStatusTip(tr("Show Preferences Window"));
		showPreferencesAct.triggered.connect(this, "showPreferences()");

		showLastFMBrowser = new QAction(new QIcon(
				"classpath://images/lastfm.png"), tr("&Last.fm Browser"), this);
		// showPreferencesAct.setShortcut(tr("Ctrl+P"));
		showLastFMBrowser.setStatusTip(tr("Opens the Last.fm Browser"));
		showLastFMBrowser.triggered.connect(this, "showLastFMBrowser()");
		
		
		aboutAct = new QAction(new QIcon(
		"classpath://images/djbrain_logo.png"), tr("&About DJBrain"), this);
		aboutAct.setStatusTip(tr("About DJBrain"));
		aboutAct.triggered.connect(this, "about()");

	}

	private void createMenus() {
		/* ============== file =================== */
		QMenu fileMenu = menuBar().addMenu(tr("&DJBrain"));
		fileMenu.addAction(newtrackAct);
		fileMenu.addAction(newPlaylistAct);

		fileMenu.addSeparator();

		/* ============== file.import =================== */
		QMenu importMenu = new QMenu(tr("Import"));
		fileMenu.addMenu(importMenu);
		importMenu.setIcon(new QIcon(rsrcPath + "/djbrain_import.png"));
		importMenu.addAction(importTracksFromMP3DirAct);
		importMenu.addAction(importTracksFromDiscogsAct);
//		importMenu.addAction(importTracksFromXMLAct);
		// importMenu.addAction(importTracksFromVirtualDJAct);
		importMenu.addSeparator();
		importMenu.addAction(importDBFromXMLAct);

		/* ============== file.export =================== */
		QMenu exportMenu = new QMenu(tr("Export"));
		fileMenu.addMenu(exportMenu);
		exportMenu.setIcon(new QIcon(rsrcPath + "/djbrain_export.png"));
//		exportMenu.addAction(exportXMLAct);
//		exportMenu.addAction(exportTracksAsXMLAct);

		fileMenu.addSeparator();
		fileMenu.addAction(showPreferencesAct);

		fileMenu.addSeparator();
		fileMenu.addAction(exitAct);

		menuBar().addSeparator();

		/* ============== tools =================== */

		QMenu toolsMenu = menuBar().addMenu(tr("&Tools"));
		toolsMenu.addAction(showTrackStackAct);
		toolsMenu.addAction(showBrainstormAct);
		toolsMenu.addAction(editGenresAct);
		toolsMenu.addAction(showLastFMBrowser);

		/* ============== help =================== */
		QMenu helpMenu = menuBar().addMenu(tr("&Help"));
		helpMenu.addAction(helpAct);
		helpMenu.addSeparator();
		helpMenu.addAction(aboutAct);
		
		QAction qtAbout = new QAction(tr("about Qt"), this);
		qtAbout.triggered.connect(QApplication.instance(),"aboutQt()");
		helpMenu.addAction(qtAbout);
		
		QAction qtjambiAbout = new QAction(tr("about QtJambi"), this);
		qtjambiAbout.triggered.connect(QApplication.instance(),"aboutQtJambi()");
		helpMenu.addAction(qtjambiAbout);
		/* ============== debug =================== */
		if (QMain.DEBUG) {
			QMenu debugMenu = menuBar().addMenu(tr("&Debug"));
			debugMenu.addAction(dumpAct);
			debugMenu.addAction(showLogAct);
		}
	}

	private QWidget getToolBar() {

		QToolButton newTrackButton = new QToolButton();
		newTrackButton.setIcon(new QIcon(rsrcPath + "/track_new.png"));
		newTrackButton.setIconSize(new QSize(25, 25));
		newTrackButton.setAutoRaise(true);
		newTrackButton.setStatusTip(tr("Create a track"));
		newTrackButton.setToolTip(tr("Create a track"));
		newTrackButton.clicked.connect(this, "newTrack()");

		QToolButton newPlaylistButton = new QToolButton();
		newPlaylistButton.setIcon(new QIcon(rsrcPath + "/playlist_new.png"));
		newPlaylistButton.setIconSize(new QSize(25, 25));
		newPlaylistButton.setAutoRaise(true);
		newPlaylistButton.setStatusTip(tr("Create a new playlist"));
		newPlaylistButton.setToolTip(tr("Create a playlist"));
		newPlaylistButton.clicked.connect(this, "newPlaylist()");

		QToolButton editGenresButton = new QToolButton();
		editGenresButton.setIcon(new QIcon(rsrcPath + "/edit_genres.png"));
		editGenresButton.setIconSize(new QSize(25, 25));
		editGenresButton.setAutoRaise(true);
		editGenresButton.setStatusTip(tr("Edit list of available genres"));
		editGenresButton.setToolTip(tr("Edit list of available genres"));
		editGenresButton.clicked.connect(this, "showEditGenresDialog()");

	
		QToolButton trackStackButton = new QToolButton();
		trackStackButton.setIcon(new QIcon(rsrcPath + "/trackstack.png"));
		trackStackButton.setIconSize(new QSize(25, 25));
		trackStackButton.setAutoRaise(true);
		trackStackButton.setStatusTip(tr("Show TrackStack window"));
		trackStackButton.setToolTip(tr("Show TrackStack window"));
		trackStackButton.clicked.connect(this, "showTrackStack()");
		
		QToolButton lastFMBrowserButton = new QToolButton();
		lastFMBrowserButton.setIcon(new QIcon(rsrcPath + "/lastfm.png"));
		lastFMBrowserButton.setIconSize(new QSize(25, 25));
		lastFMBrowserButton.setAutoRaise(true);
		lastFMBrowserButton.setStatusTip(tr("Open the Last.fm Browser"));
		lastFMBrowserButton.setToolTip(tr("Open the Last.fm Browser"));
		lastFMBrowserButton.clicked.connect(LastFMBrowser.getInstance(), "showWindow()");

		QWidget myToolBar = new QWidget(this);
		myToolBar.setAcceptDrops(false);

		QBoxLayout layout = new QBoxLayout(QBoxLayout.Direction.LeftToRight);
		layout.setMargin(0);

		myToolBar.setFixedHeight(30);

		layout.addWidget(newTrackButton);
		layout.addWidget(newPlaylistButton);
		layout.addWidget(editGenresButton);
		layout.addWidget(trackStackButton);
		layout.addWidget(lastFMBrowserButton);
		
		layout.insertStretch(10);
		myToolBar.addAction(newtrackAct);
		myToolBar.addAction(newPlaylistAct);
		myToolBar.addAction(editGenresAct);
		myToolBar.addAction(showTrackStackAct);
	
		
		layout.addWidget(searchWidget);

		myToolBar.setLayout(layout);
		return myToolBar;
	}

	private void createStatusBar() {
		statusBar().showMessage(tr("Ready"));
	}

	/**
	 * @param playListId
	 */
	public void playlistSelected(int playListId) {
		tabbedTracklist.showTracksForPlaylistID(playListId);
	}

	/**
	 * @param statusBarText
	 */
	public void setStatusBarText(String statusBarText) {
		statusBar().showMessage(statusBarText, DEFAULT_STATUSBAR_TIME);
	}

	
	@SuppressWarnings("unused")
	private void showPreferences() {
		PreferencesWindow.showPreferences(signalHub);
	}

	

	@SuppressWarnings("unused")
	private void showTrackStack() {
		TrackStackWidget.getInstance(signalHub).showWindow();
	}
	
	@SuppressWarnings("unused")
	private void showLastFMBrowser(){
		LastFMBrowser.getInstance().showWindow();
	}


	@SuppressWarnings("unused")
	private void importFromDiscogs() {

//		String s = QFileDialog.getOpenFileName(this, "Choose a file", System
//				.getProperty("user.dir"), "Excel sheet (*.xls)");
//
//		List<Track> tracklist;
//		try {
//			tracklist = DiscogsParser.getTracklist(new File(s));
//
//			QMessageBox.StandardButton ret = QMessageBox
//					.question(
//							this,
//							tr("Import Summary"),
//							tr("This will add "
//									+ tracklist.size()
//									+ " tracks to the Library\n"
//									+ "Do you want to check each file? By clicking \"No All\" all tracks will\n be inserted directly into the database?"),
//							new QMessageBox.StandardButtons(
//									QMessageBox.StandardButton.YesToAll,
//									QMessageBox.StandardButton.NoToAll,
//									QMessageBox.StandardButton.Cancel),
//							QMessageBox.StandardButton.YesToAll);
//			if (ret == QMessageBox.StandardButton.YesToAll) {
//
//				// for (Track track : tracklist) {
//
//				CheckNewTrackWidget.checkTracks(signalHub, tracklist).show();
//				// }
//
//			} else if (ret == QMessageBox.StandardButton.NoToAll) {
//				for (Track track : tracklist) {
//					DBConnection.getInstance().insertTrack(track);
//				}
//
//			}
//			signalHub.signalTrackCountChanged();
//
//		} catch (IOException ioe) {
//			QMessageBox.critical(this, tr("Error!"), tr("Error occured:"
//					+ ioe.getMessage()));
//		}
	}

	// @SuppressWarnings("unused")
	// private void importTracksFromVirtualDJ() {
	//
	// String s = QFileDialog.getOpenFileName(this, "Choose a file", System
	// .getProperty("user.dir"), "xml (*.xml)");
	//
	// ArrayList<Track> tracklist;
	// try {
	// tracklist = VirtualDJImporter.importTracks(new File(s));
	//
	// int ret = QMessageBox
	// .question(
	// this,
	// tr("Import Summary"),
	// tr("This will add "
	// + tracklist.size()
	// + " tracks to the Library\n"
	// + "Do you want to check each file? By clicking \"No All\" all tracks
	// will\n be inserted directly into the database?"),
	// QMessageBox.YesAll | QMessageBox.Default,
	// QMessageBox.NoAll, QMessageBox.Cancel
	// | QMessageBox.Escape);
	// if (ret == QMessageBox.YesAll) {
	//
	// CheckNewTrackWidget.getInstance(tracklist).show();
	//
	// } else if (ret == QMessageBox.NoAll) {
	// for (Track track : tracklist) {
	// DBConnection.getInstance().insertTrack(track);
	// }
	// }
	// signalHub.signalTrackCountChanged();
	// } catch (Exception ioe) {
	// QMessageBox.critical(this, tr("Error!"), tr("Error occured:"
	// + ioe.getMessage()));
	// }
	// }

	@SuppressWarnings("unused")
	private void importTracksFromMP3Dir() {
		System.err.println("importTracksFromMP3Dir() was called!");
		String s = QFileDialog.getExistingDirectory(this,
				"Choose a Dir to scan", System.getProperty("user.dir"));

		if (s.equals("")) {
			// cancel button clicked:
			return;
		}

		List<Track> tracks;
		try {
			tracks = MediaImporter.getTracksForDirectory(new File(s));

			if (tracks == null) {
				logger.debug("importTracksFromMP3Dir() was canceled");
				return;
			}

			if (tracks.size() > 0) {

				QMessageBox.StandardButton ret = QMessageBox
						.question(
								this,
								tr("Import Summary"),
								tr("This will add "
										+ tracks.size()
										+ " tracks to the Library\n"
										+ "Do you want to check each file? By clicking \"No All\" all tracks will\n be inserted directly into the database?"),
								new QMessageBox.StandardButtons(
										QMessageBox.StandardButton.YesToAll,
										QMessageBox.StandardButton.NoToAll,
										QMessageBox.StandardButton.Cancel),
								QMessageBox.StandardButton.YesToAll);
				if (ret == QMessageBox.StandardButton.YesToAll) {

					// CheckNewTrackWidget c =
					// CheckNewTrackWidget.getInstance(signalHub);
					// c.show();

					CheckNewTrackWidget.checkTracks(signalHub, tracks).show();

				} else if (ret == QMessageBox.StandardButton.NoToAll) {
					for (Track track : tracks) {
						DBConnection.getInstance().insertTrack(track);
					}
				}
				signalHub.signalTrackCountChanged();

			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
			QMessageBox.critical(this, tr("Error!"), tr("Error occured:"
					+ ioe.getMessage()));
		}

	}

	

	
	@SuppressWarnings("unused")
	private void showHelpBrowser() {
		HelpBrowserWidget.showHelp(HelpBrowserWidget.Pages.HELP);
	}

	@SuppressWarnings("unused")
	private void showEditGenresDialog() {
		QEditGenres.getInstance(signalHub).show();
	}

	@SuppressWarnings("unused")
	private void showBrainstorm() {
		BrainStormWidget.show(signalHub);
	}

	@SuppressWarnings("unused")
	private void closeWindowWithoutConfirm() {
		confirmClose = false;
		DBConnection.getInstance().shutdown();
//		this.close();
		QApplication.quit();

		System.exit(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trolltech.qt.gui.QWidget#closeEvent(com.trolltech.qt.gui.QCloseEvent)
	 */
	@Override
	protected void closeEvent(QCloseEvent event) {
		if (confirmClose) {
			QMessageBox.StandardButton ret = QMessageBox.warning(this,
					tr("Quit DJBrain?"),
					tr("Do you really want to quit DJBrain?"),
					new QMessageBox.StandardButtons(
							QMessageBox.StandardButton.Yes,
							QMessageBox.StandardButton.Cancel),
					QMessageBox.StandardButton.Yes);
			if (ret == QMessageBox.StandardButton.Yes) {

				DBConnection.getInstance().shutdown();
				super.closeEvent(event);
				System.exit(0);
			} else {

				event.ignore();

			}
		} else {
			DBConnection.getInstance().shutdown();

			super.closeEvent(event);
			System.exit(0);
		}
	}

	/**
	 * 
	 * @return SuggestedTrackList widget
	 */
	public SuggestedTrackList getSuggestedTrackList() {
		return currentTrack.getSuggestedTrackList();
	}

	/**
	 * @return the Tracklist widget
	 */
	public TrackList getTrackList() {
		return tabbedTracklist.getCurrentTrackList();
	}

	/**
	 * @return the PlaylistList widget
	 */
	public PlaylistListWidget getPlaylist() {
		return playlistListWidget;
	}

	/*
	 * is called from "debug" in menu
	 */
	@SuppressWarnings("unused")
	private void showLogWindow() {
		LogWindow.showLogWindow();

	}
}
