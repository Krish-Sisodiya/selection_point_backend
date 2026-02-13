package com.selection_point.service;

import com.selection_point.entity.Batch;
import com.selection_point.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    @Transactional
    public Batch getOrCreateBatch(String coursePackage) {

        return batchRepository
                .findAvailableBatch(coursePackage)
                .orElseGet(() -> {

                    Batch batch = new Batch();
                    batch.setCoursePackage(coursePackage);
                    batch.setBatchName(
                            coursePackage.toUpperCase().replace(" ", "_")
                                    + "-" + (batchRepository.count() + 1)
                    );
                    batch.setCapacity(30);
                    batch.setCurrentStrength(0);

                    return batchRepository.saveAndFlush(batch); // 🔥 IMPORTANT
                });
    }
}
