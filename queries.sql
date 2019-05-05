use spamizer;

# Select per trobar el totalwrods, amb laplace smoothing (k)
select count(*) as totalword from (
                                      SELECT word FROM spam
                                      UNION
                                      SELECT word FROM ham) as t;

# inserts de prova a les taules spam i ham
insert into spam (`word`,`times`) values ("SECRET",3);
insert into spam (`word`,`times`) values ("OFFER",1);
insert into spam (`word`,`times`) values ("SPORTS",1);
insert into spam (`word`,`times`) values ("IS",1);
insert into spam (`word`,`times`) values ("LINK",2);
insert into spam (`word`,`times`) values ("CLICK",1);

insert into ham (`word`,`times`) values ("PLAY",2);
insert into ham (`word`,`times`) values ("TODAY",2);
insert into ham (`word`,`times`) values ("SPORTS",5);
insert into ham (`word`,`times`) values ("IS",1);
insert into ham (`word`,`times`) values ("WENT",1);
insert into ham (`word`,`times`) values ("SECRET",1);
insert into ham (`word`,`times`) values ("COSTS",1);
insert into ham (`word`,`times`) values ("EVENT",1);
insert into ham (`word`,`times`) values ("MONEY",1);

TRUNCATE spam;




create table messages (
                          id int auto_increment primary key,
                          spam int not null,
                          ham int not null
);

insert into messages(`id`,`spam`,`ham`) values(1,3,5);

# Select que calcula la probabilitat de Spam
select spam/(ham+spam) as pTSpam from messages where id=1;
# Select que calcula la probabilitat de Ham
select ham/(ham+spam) as pTHam from messages where id=1;

# Select A*B*C
select sum(ln((times+1)/((select count(*)+1*2 as totalword from (
                                                                    SELECT word FROM spam
                                                                    UNION
                                                                    SELECT word FROM ham) as t)+1*2))) as NaiveP
from spam where word in ("TODAY","IS","SECRET");


# Select A*B*C amb exp
select exp(sum(ln((times+1)/((select count(*) as totalword from (
                                                                    SELECT word FROM spam
                                                                    UNION
                                                                    SELECT word FROM ham) as t))))) as NaiveP
from spam where word in ("TODAY","IS","SECRET");

# 21
select count(*) + (select sum(times) from spam) as totalword from ( SELECT word FROM spam  UNION SELECT word FROM ham) as t;


select exp(sum(ln((times+1)/((select count(*) + (select sum(times) from spam) as totalword from ( SELECT word FROM spam  UNION SELECT word FROM ham)  as t)))))
from
    (select v.word, coalesce(times,0) as times
     from(
             Select word
             from spam where word in ("TODAY","IS","SECRET")
             UNION
             SELECT "TODAY" AS word
         ) as v left join spam on v.word = spam.word) as m;