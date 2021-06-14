package model

import rules.Rule

case class JobGroupRequest(rules: Rule, sponsoredPublishers: List[Publisher],
                           priority: Int)
