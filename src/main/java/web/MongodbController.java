package web;

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
@RequestMapping("/mongodb/test-collection")
public class MongodbController {

  @Autowired
  private MongoTemplate mongoTemplate;

  @PostMapping
  public Object insert(
    @RequestBody TestCollection testCollection
  ) {
    return mongoTemplate.insert(testCollection);
  }

  @DeleteMapping("{code}")
  public Object remove(
    @PathVariable String code
  ) {
    return mongoTemplate.remove(buildQuery(code), TestCollection.class);
  }

  @PutMapping("{code}/update")
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


  @PutMapping("{code}/findAndModify")
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

  @GetMapping("{code}")
  public Object findOne(
    @PathVariable String code
  ) {
    return mongoTemplate.findOne(buildQuery(code), TestCollection.class);
  }

  private Query buildQuery(final String code) {
    return Query.query(Criteria.where("code").is(code));
  }
}
