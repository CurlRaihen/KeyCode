package com.group7.project.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group7.project.model.Keycard;
import com.group7.project.repository.KeyCardRepository;

@Service
public class KeyCardService {

    @Autowired
    KeyCardRepository keyCardRepository;

    public KeyCardService(KeyCardRepository keyCardRepository) {
        this.keyCardRepository = keyCardRepository;
    }

    public void addDataToDatabase(int id, String benutzername, Date anfangstag, Date endtag, String status) {

        Keycard newCard = new Keycard();

        newCard.setId(id);
        newCard.setBenutzername(benutzername);
        newCard.setAnfangstag(anfangstag);
        newCard.setEndtag(endtag);
        newCard.setStatus(status);

        keyCardRepository.save(newCard);

    }

    public List<Keycard> listAll() {
        return keyCardRepository.findAll();
    }

}
