-- 使用navicat创建存储过程，把这段逻辑copy过去，并调用它
BEGIN
  DECLARE c_id INT DEFAULT 1;
  while c_id <= 50000 do
    insert into goods(name, count) VALUES(concat('红糖玛奇朵',c_id), 20);
    set c_id=c_id+1;
end while;