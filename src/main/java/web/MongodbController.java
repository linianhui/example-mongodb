package web;

import java.util.UUID;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongodb")
public class MongodbController {

  @Autowired
  private MongoTemplate mongoTemplate;

  @PostMapping("test-collection")
  public Object insert(
      @RequestBody TestCollection testCollection
  ) {
    final DateTime now = DateTime.now();
    testCollection.setCreatedAt(now);
    testCollection.setUpdatedAt(now);
    return mongoTemplate.insert(testCollection);
  }

  @DeleteMapping("test-collection/{code}")
  public Object remove(
      @PathVariable String code
  ) {
    return mongoTemplate.remove(buildQuery(code), TestCollection.class);
  }

  @PutMapping("test-collection/{code}/update")
  public Object updateFirst(
      @PathVariable String code,
      @RequestBody TestCollection testCollection
  ) {
    Update update = Update
        .update("name", testCollection.getName())
        .set("updatedAt", DateTime.now());
    return mongoTemplate.updateFirst(
        buildQuery(code),
        update,
        TestCollection.class
    );
  }

  @PutMapping("test-collection/{code}/findAndModify")
  public Object findAndModify(
      @PathVariable String code,
      @RequestBody TestCollection testCollection
  ) {
    Update update = Update
        .update("name", testCollection.getName())
        .set("updatedAt", DateTime.now());
    return mongoTemplate.findAndModify(
        buildQuery(code),
        update,
        FindAndModifyOptions.options().returnNew(true).upsert(false),
        TestCollection.class
    );
  }

  @GetMapping("test-collection/{code}")
  public Object findOne(
      @PathVariable String code
  ) {
    return mongoTemplate.findOne(buildQuery(code), TestCollection.class);
  }

  @GetMapping("test-collection")
  public Object findList(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime updatedAt_Gte,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime updatedAt_Lte
  ) {
    final Criteria criteria = Criteria.where("updatedAt")
        .gte(updatedAt_Gte)
        .lte(updatedAt_Lte);
    final Query query = Query.query(criteria);
    return mongoTemplate.find(query, TestCollection.class);
  }

  private Query buildQuery(final String code) {
    return Query.query(Criteria.where("code").is(code));
  }

  @PutMapping("test-collection/{code}/dirty-read")
  public void dirtyReadTest(
      @PathVariable String code
  ) {
    final String writeName = UUID.randomUUID().toString();
    final Update update = Update.update("name", writeName);
    final Query query = buildQuery(code);
    mongoTemplate.updateFirst(
        query, update, TestCollection.class
    );

    final TestCollection testCollection = mongoTemplate.findOne(query, TestCollection.class);
    if (testCollection == null) {
      mongoTemplate.insert(WriteReadLog.builder()
          .write(writeName)
          .build());
      return;
    }
    final String readName = testCollection.getName();
    if (!writeName.equals(readName)) {
      mongoTemplate.insert(WriteReadLog.builder()
          .write(writeName)
          .read(readName)
          .build());
    }
  }

  @GetMapping("dirty-read-log")
  public Object getWriteReadLog() {
    return mongoTemplate.findAll(WriteReadLog.class);
  }

  @DeleteMapping("dirty-read-log")
  public void deleteWriteReadLog() {
    mongoTemplate.dropCollection(WriteReadLog.class);
  }
}
