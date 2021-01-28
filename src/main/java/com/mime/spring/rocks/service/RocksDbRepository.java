package com.mime.spring.rocks.service;


import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class RocksDbRepository implements KVRepository<String, Object> {
    private static final String FILE_NAME = "spring-boot-db";
    private File baseDir;
    private RocksDB rocksDB;

    private static final Logger logger = LoggerFactory.getLogger(RocksDbRepository.class);


    @PostConstruct
    private void initialize() {
        RocksDB.loadLibrary();

        final Options options = new Options();
        options.setCreateIfMissing(true);

        baseDir = new File("tmp/db", FILE_NAME);

        try {
            Files.createDirectories(baseDir.getParentFile().toPath());
            Files.createDirectories(baseDir.getAbsoluteFile().toPath());
            rocksDB = RocksDB.open(options, baseDir.getAbsolutePath());

            logger.info("RocksDb initialized!!!");
        } catch (IOException | RocksDBException e) {
            logger.error("Error initializing RocksDB. Exception: '{}', message: '{}'", e.getCause(), e.getMessage(), e);
        }
    }

    @Override
    public synchronized boolean save(String key, Object value) {
        logger.info("saving value '{}' with key '{}'", value, key);

        try {
            rocksDB.put(key.getBytes(), SerializationUtils.serialize(value));
        } catch (RocksDBException e) {
            logger.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public synchronized  Optional<Object> find(String key) {
        Object value = null;

        try {
            byte[] bytes = rocksDB.get(key.getBytes());
            if (bytes != null) {
                value = SerializationUtils.deserialize(bytes);
            }
        } catch (RocksDBException e) {
            logger.error("Error retrieving the entry with key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
        }

        logger.info("finding key '{}' returns '{}'", key, value);
        return Optional.ofNullable(value);
    }

    @Override
    public synchronized boolean delete(String key) {
        logger.info("deleting key '{}'", key);

        try {
            rocksDB.delete(key.getBytes());
        } catch(RocksDBException e) {
            logger.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
            return false;
        }

        return true;
    }
}
