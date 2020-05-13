package web;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    Update update = Update.update("name", testCollection.getName());
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
    Update update = Update.update("name", testCollection.getName());
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
