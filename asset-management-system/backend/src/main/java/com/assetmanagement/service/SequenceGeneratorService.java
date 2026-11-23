package com.assetmanagement.service;

import com.assetmanagement.model.DatabaseSequence;
import com.assetmanagement.model.Laptop;
import com.assetmanagement.model.Mobile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public synchronized long generateSequence(String seqName) {
        syncSequenceBaselineIfNeeded(seqName);

        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class);

        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    public synchronized long peekSequence(String seqName) {
        syncSequenceBaselineIfNeeded(seqName);

        DatabaseSequence counter = mongoOperations.findById(seqName, DatabaseSequence.class);
        long current = (counter != null) ? counter.getSeq() : 0;
        return current + 1;
    }

    public String generateLaptopId() {
        long seq = generateSequence("laptop_sequence");
        return String.format("LAP-%04d", seq);
    }

    public String peekNextLaptopId() {
        long next = peekSequence("laptop_sequence");
        return String.format("LAP-%04d", next);
    }

    public String generateMobileId() {
        long seq = generateSequence("mobile_sequence");
        return String.format("MOB-%04d", seq);
    }

    public String peekNextMobileId() {
        long next = peekSequence("mobile_sequence");
        return String.format("MOB-%04d", next);
    }

    public synchronized void ensureSequenceAtLeast(String seqName, long minSeq) {
        DatabaseSequence counter = mongoOperations.findById(seqName, DatabaseSequence.class);
        if (counter == null || counter.getSeq() < minSeq) {
            mongoOperations.save(new DatabaseSequence(seqName, minSeq));
        }
    }

    private void syncSequenceBaselineIfNeeded(String seqName) {
        DatabaseSequence counter = mongoOperations.findById(seqName, DatabaseSequence.class);
        if (counter != null && counter.getSeq() > 0) {
            return;
        }

        long maxFound = 0;
        if ("laptop_sequence".equals(seqName)) {
            Pattern pattern = Pattern.compile("^(?:LAP|LPT)-0*(\\d+)$", Pattern.CASE_INSENSITIVE);
            for (Laptop laptop : mongoOperations.findAll(Laptop.class)) {
                if (laptop.getLaptopId() != null) {
                    Matcher m = pattern.matcher(laptop.getLaptopId().trim());
                    if (m.matches()) {
                        try {
                            long val = Long.parseLong(m.group(1));
                            if (val > maxFound) maxFound = val;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } else if ("mobile_sequence".equals(seqName)) {
            Pattern pattern = Pattern.compile("^MOB-0*(\\d+)$", Pattern.CASE_INSENSITIVE);
            for (Mobile mobile : mongoOperations.findAll(Mobile.class)) {
                if (mobile.getMobileId() != null) {
                    Matcher m = pattern.matcher(mobile.getMobileId().trim());
                    if (m.matches()) {
                        try {
                            long val = Long.parseLong(m.group(1));
                            if (val > maxFound) maxFound = val;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        mongoOperations.save(new DatabaseSequence(seqName, maxFound));
    }
}
