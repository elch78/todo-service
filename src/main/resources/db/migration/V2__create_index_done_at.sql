-- index to support query fun findByDoneAtIsNull(): Iterable<TodoItem>
CREATE INDEX idx_todo_item_done_at ON todo_item(done_at);
