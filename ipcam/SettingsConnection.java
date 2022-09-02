/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoResampler;

/**
 *
 * @author katen'ka
 */
public class SettingsConnection {

    public SettingsConnection () {
        resampler = null;
        videoStreamId = -1;
        coder = null;
        decoder = null;
        readContainer = null;
        writeContainer = null;
        writer = null;
    }

    public void setReadContainer(IContainer container) {
        this.readContainer = container;
    }
    
    public IContainer getReadContainer() {
        return readContainer;
    }

    public void setWriteContainer(IContainer container) {
        this.writeContainer = container;
    }

    public IContainer getWriteContainer() {
        return writeContainer;
    }

    public void setStreamCoder(IStreamCoder coder) {
        this.coder = coder;
    }
    
    public IStreamCoder getStreamCoder() {
        return coder;
    }
    
    public void setStreamDecoder(IStreamCoder coder) {
        this.decoder = coder;
    }
    
    public IStreamCoder getStreamDecoder() {
        return decoder;
    }
    
    public void setVideoStreamId(int id) {
        this.videoStreamId = id;
    }
    
    public int getVideoStreamId() {
        return videoStreamId;
    }
    
    public void setVideoResampler(IVideoResampler resampler) {
        this.resampler = resampler;
    }
    
    public IVideoResampler getVideoResampler() {
        return resampler;
    }

    public void setWriter(IMediaWriter writer) {
        this.writer = writer;
    }

    public IMediaWriter getWriter() {
        return writer;
    }

    private IVideoResampler resampler = null;
    private int videoStreamId = -1;
    private IStreamCoder coder = null;
    private IStreamCoder decoder = null;
    private IContainer readContainer = null;
    private IContainer writeContainer = null;
    private IMediaWriter writer = null;
}
