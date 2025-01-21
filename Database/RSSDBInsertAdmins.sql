use StarsectorBlogpostRSSDB

insert into [Login]([Alias], [PasswordPlain])
values
('asd', 'dsa')
go

insert into [Login]([Alias], [PasswordPlain])
values
('user', 'user')
go

insert into [Administrator]([LoginID])
values
((select top 1 l.IDLogin from [Login] as l where l.[Alias] = 'asd'))
go