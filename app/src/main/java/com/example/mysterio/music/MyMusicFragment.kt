package com.example.mysterio.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mysterio.R
import com.example.mysterio.databinding.FragmentMusicBinding
import es.claucookie.miniequalizerlibrary.EqualizerView

class MyMusicFragment : Fragment(), MusicAdapter.SongClicks {

    private lateinit var musicAdapter: MusicAdapter
    private lateinit var binding: FragmentMusicBinding
    private var runningIndex = 0
    lateinit var music: List<SongModel>
    var musicService: MusicService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            musicService?.initialiseSeekBar(binding.seekBar)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    private var isMusicPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_music, container, false
        )
        initializeView()
        return binding.root

    }

    private fun initializeView() {

        music = getSongsList()
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        musicAdapter = MusicAdapter(requireContext(), music, this)
        binding.rvList.adapter = musicAdapter
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        prepare()
        binding.btnPlayPause.setOnClickListener {
            if (isMusicPlaying) {
                pause()
            } else {
                play()
            }
        }

        binding.btnPrevious.setOnClickListener { playPrevious() }
        binding.btnNext.setOnClickListener { playNext() }
    }

    private fun getSongsList(): List<SongModel> {
        return listOf(
            SongModel(R.raw.two, getMp3FileName(R.raw.two), getMp3FileLength(R.raw.two)),
            SongModel(R.raw.one, getMp3FileName(R.raw.one), getMp3FileLength(R.raw.one)),
            SongModel(R.raw.three, getMp3FileName(R.raw.three), getMp3FileLength(R.raw.three)),
            SongModel(R.raw.four, getMp3FileName(R.raw.four), getMp3FileLength(R.raw.four)),
            SongModel(R.raw.five, getMp3FileName(R.raw.five), getMp3FileLength(R.raw.five)),
            SongModel(R.raw.six, getMp3FileName(R.raw.six), getMp3FileLength(R.raw.six)))
    }

    private fun play() {
        if (!isMusicPlaying) {
            musicService?.play()
            isMusicPlaying = true
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            musicAdapter?.notifyDataSetChanged()
        }
    }

    private fun pause() {
        if (isMusicPlaying) {
            musicService?.pause()
            isMusicPlaying = false
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            musicAdapter?.notifyDataSetChanged()
        }
    }


    private fun playNext() {
        if (runningIndex + 1 < music.size) {
            runningIndex = runningIndex + 1
        } else {
            runningIndex = 0
        }
        onSongClicked(music.get(runningIndex), runningIndex)
    }

    private fun playPrevious() {
        if (runningIndex - 1 >= 0) {
            runningIndex = runningIndex - 1
        } else {
            runningIndex = music.size - 1
        }
        onSongClicked(music.get(runningIndex), runningIndex)
    }

    override fun onSongClicked(music: SongModel, position: Int) {
        val musicIntent = Intent(context, MusicService::class.java)
        musicIntent.putExtra(
            "music_file_path",
            "android.resource://" + context?.packageName + "/" + music.resourceId
        )
        context?.startService(musicIntent)
        context?.bindService(musicIntent, connection, Context.BIND_AUTO_CREATE)
        binding.tvSong.text = getMp3FileName(music.resourceId)
        runningIndex = position
        musicAdapter?.currentIndex = runningIndex
        musicAdapter?.notifyDataSetChanged()
        isMusicPlaying = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        if (getMp3Thumbnail(music.resourceId) != null) {
            binding.imgAlbumArt.setImageBitmap(getMp3Thumbnail(music.resourceId))
            binding.imgBackground.setImageBitmap(getMp3Thumbnail(music.resourceId))
        } else {
            binding.imgAlbumArt.setImageResource(R.drawable.ic_music)
            binding.imgBackground.setImageResource(R.drawable.ic_music)
        }

    }

    override fun playBarAnimation(equalizerView: EqualizerView) {
        if (isMusicPlaying) {
            equalizerView.animateBars()
        } else {
            equalizerView.stopBars()
        }
    }

    private fun getMp3Thumbnail(mp3ResourceId: Int): Bitmap? {
        val retriever = MediaMetadataRetriever()
        val fileDescriptor: AssetFileDescriptor = resources.openRawResourceFd(mp3ResourceId)
        retriever.setDataSource(
            fileDescriptor.fileDescriptor,
            fileDescriptor.startOffset,
            fileDescriptor.length
        )
        val embeddedPicture: ByteArray? = retriever.embeddedPicture
        if (embeddedPicture != null) {
            return BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
        }
        retriever.release()
        return null
    }

    private fun getMp3FileName(mp3ResourceId: Int): String {
        val retriever = MediaMetadataRetriever()
        val fileDescriptor: AssetFileDescriptor = resources.openRawResourceFd(mp3ResourceId)
        retriever.setDataSource(
            fileDescriptor.fileDescriptor,
            fileDescriptor.startOffset,
            fileDescriptor.length
        )
        val title: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        retriever.release()
        return title ?: ""
    }

    private fun getMp3FileLength(mp3ResourceId: Int): String {
        val retriever = MediaMetadataRetriever()
        val fileDescriptor: AssetFileDescriptor = resources.openRawResourceFd(mp3ResourceId)
        retriever.setDataSource(
            fileDescriptor.fileDescriptor,
            fileDescriptor.startOffset,
            fileDescriptor.length
        )
        val duration: Long =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        retriever.release()
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return "$minutes:${String.format("%02d", seconds)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unbindService(connection)
    }

    fun prepare() {
        val musicIntent = Intent(context, MusicService::class.java)
        musicIntent.putExtra(
            "music_file_path",
            "android.resource://" + context?.packageName + "/" + R.raw.two
        )
        context?.startService(musicIntent)
        context?.bindService(musicIntent, connection, Context.BIND_AUTO_CREATE)
        binding.tvSong.text = getMp3FileName(R.raw.two)
        if (getMp3Thumbnail(R.raw.two) != null) {
            binding.imgAlbumArt.setImageBitmap(getMp3Thumbnail(R.raw.two))
            binding.imgBackground.setImageBitmap(getMp3Thumbnail(R.raw.two))
        } else {
            binding.imgAlbumArt.setImageResource(R.drawable.ic_music)
            binding.imgBackground.setImageResource(R.drawable.ic_music)
        }
    }

}