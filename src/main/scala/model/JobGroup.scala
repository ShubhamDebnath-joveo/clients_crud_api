package model

import rules.Rule
import org.bson.types.ObjectId

import java.time.LocalDateTime

case class JobGroup(_id: ObjectId = new ObjectId,
                    rules: String, sponsoredPublishers: List[Publisher] = List[Publisher](),
                    priority: Int = 10, createdDate:LocalDateTime = LocalDateTime.now())
