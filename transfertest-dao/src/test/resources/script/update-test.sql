insert into users(name) values('Rodger');
insert into users(name) values('Mary');


insert into accounts(accountnumber,currency,balance,userid) values('RRR1234567890RRR1234567890','EUR','100',select userid from users where name='Rodger');
insert into accounts(accountnumber,currency,balance,userid) values('RRR1234567891RRR1234567891','USD','0',select userid from users where name='Rodger');

insert into accounts(accountnumber,currency,balance,userid) values('MMM9876543211MMM9876543211','EUR','10',select userid from users where name='Mary');
insert into accounts(accountnumber,currency,balance,userid) values('MMM9876543211MMM9876543212','USD','100',select userid from users where name='Mary');