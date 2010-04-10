/**
 * 
 */
package net.z0id.djbrain.imexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.z0id.djbrain.db.GenreCache;
import net.z0id.djbrain.objects.Track;

import org.apache.log4j.Logger;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v2.ID3V2Tag;

import com.trolltech.qt.gui.QProgressDialog;
import com.trolltech.qt.gui.QWidget;

/**
 * @author meatz
 * 
 */
public class MediaImporter {

	private static Logger logger = Logger.getLogger(MediaImporter.class);

//	private static Track getTrackForOgg(File scanfile) {
//		// VorbisAudioFileFormat v =
//		try {
//			Track track = new Track();
//			VorbisFile v = new VorbisFile(scanfile.getCanonicalPath());
//			Comment c = v.getComment()[0];
//
//			String artist = c.query("ARTIST");
//			if (artist != null)
//				track.setArtist(artist);
//
//			String title = c.query("TITLE");
//			if (title != null)
//				track.setTrackname(title);
//
//			String album = c.query("ALBUM");
//			if (album != null)
//				track.setLabel(album);
//
//			String comment = c.query("COMMENT");
//			if (comment != null)
//				track.setComment(comment);
//
//			track.setFilename(scanfile.getCanonicalPath());
//			track.setMediatype(Track.MediaTypes.MP3);
//			return track;
//		} catch (Exception e) {
//			logger.error(e);
//			return null;
//		}
//	}

	/**
	 * @param file
	 * @return Track representation of this mp3file
	 * @throws TagException
	 * @throws IOException
	 * 
	 */
	public static Track getTrackForMp3(File file) {
		MP3File mp3file = null;

		mp3file = new MP3File(file);

		Track track = new Track();

		ID3V2Tag id3tag = null;
		try {
			id3tag = mp3file.getID3V2Tag();
		} catch (ID3Exception e1) {

			e1.printStackTrace();
			return null;
		}

		if (id3tag != null) {
			// AbstractID3v2 id3tag = mp3file.getID3v2Tag();
			logger.debug("checking file with: 2 --> " + file.getAbsolutePath());
			track.setFilename(file.getAbsolutePath());
			track.setMediatype("Mp3");

			track.setArtist(id3tag.getArtist());

			track.setTrackname(id3tag.getTitle());
			track.setLabel(id3tag.getAlbum());
			track.setComment(id3tag.getComment());

			String genre = id3tag.getGenre();

			int genreId = GenreCache.getIDForGenre(genre);

			if (genreId == 0) {
				GenreCache.addGenre(genre);
				int id = GenreCache.getIDForGenre(genre);

				track.setGenreId(id);
			} else {
				track.setGenreId(genreId);
			}

			try {
				track.setReleased(id3tag.getYear());
			} catch (ID3Exception e) {

			}

		} else {

			ID3V1Tag tag = null;
			try {
				tag = mp3file.getID3V1Tag();
			} catch (ID3Exception e1) {

				e1.printStackTrace();

				return null;
			}

			if (tag != null) {
				logger.debug("checking file with: 1 --> "
						+ file.getAbsolutePath());
				track.setArtist(tag.getArtist());
				track.setMediatype("Mp3");
				track.setFilename(file.getAbsolutePath());
				track.setTrackname(tag.getTitle());
				track.setLabel(tag.getAlbum());
				track.setComment(tag.getComment());
				try {
					int year = Integer.parseInt(tag.getYear());
					track.setReleased(year);
				} catch (NumberFormatException nfe) {
					// ignore me
				}

				// genre is not set due to 1byte genre limitations of id3v1...

			} else {
				// theres no id3tag available...
				logger.debug("checking file with: none --> "
						+ file.getAbsolutePath());
				track.setMediatype("Mp3");
				track.setFilename(file.getAbsolutePath());
			}

		}
		return track;
	}

	private static List<File> searchFiles(List<File> allFiles, File file) {
		logger.trace("searchFiles() called");
		if (file.isDirectory()) {
			// System.out.println("found dir:" + file.getAbsolutePath());
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				searchFiles(allFiles, files[i]);
			}
		} else {
			// System.out.println("found file:" + file.getAbsolutePath());
			if ((file.getName().toLowerCase().endsWith("mp3") || file.getName()
					.toLowerCase().endsWith("ogg"))
					&& file.canRead()) {
				logger.debug("found file: " + file.toString());
				allFiles.add(file);
			}
		}
		logger.trace("searchFiles() finished");
		return allFiles;
	}

	/**
	 * @param qmain
	 * @param file
	 * @return ArrayList<Track>
	 */
	// public ArrayList<Track> getTracksForDirectory( QMain qmain, File file) {
	public static List<Track> getTracksForDirectory(File file) {

		// TODO start this in new thread
		List<File> allFiles = new LinkedList<File>();
		allFiles = searchFiles(allFiles, file);

		List<Track> allTracks = new ArrayList<Track>(allFiles.size());

		QProgressDialog progress = new QProgressDialog("Scanning  files...",
				"Abort", 0, allFiles.size(), new QWidget());

		progress.setAutoClose(true);
		progress.setMaximum(allFiles.size());
		progress.show();
		int i = 0;

		for (File scanfile : allFiles) {
			i++;
			progress.setValue(i);

			if (scanfile.getName().toLowerCase().endsWith("mp3")) {

				Track foo = getTrackForMp3(scanfile);
				if (foo != null) {
					allTracks.add(foo);
				}
			}
//			else if (scanfile.getName().toLowerCase().endsWith("ogg")) {
//
//				Track foo = getTrackForOgg(scanfile);
//				if (foo != null) {
//					allTracks.add(foo);
//				}
//			}

			if (progress.wasCanceled())
				return null;
		}

		return allTracks;
	}

	/**
	 * physically removes this Track from the HD
	 * 
	 * @param track
	 *            the Track that should be deleted
	 * @return true if the track was deleted, false on any error
	 */
	public static boolean deleteTrackFromHD(Track track) {

		File file = new File(track.getFilename());
		if (file.canWrite()) {
			if (file.delete()) {
				return true;
			}
		}
		return false;
	}

}
