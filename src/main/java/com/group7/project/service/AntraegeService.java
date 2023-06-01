package com.group7.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group7.project.model.Antrag;
import com.group7.project.repository.AntraegeRepository;

@Service
public class AntraegeService {

    @Autowired
    private AntraegeRepository antraegeRepository;

    public AntraegeService(AntraegeRepository antraegeRepository) {
        this.antraegeRepository = antraegeRepository;
    }

    public List<Antrag> listAll() {
        return antraegeRepository.findAll();
    }

}
