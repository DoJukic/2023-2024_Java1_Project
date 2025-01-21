use StarsectorBlogpostRSSDB

CREATE TABLE [Login]
(
	[IDLogin]		int				identity,		constraint [Login_PK] primary key ([IDLogin]),
	[Alias]			nvarchar(max)	not null,
    [PasswordPlain]	nvarchar(max)	not null,
);
go

create table [Administrator]
(
    [LoginID]		int				not null,		constraint [Administrator_PK] primary key ([LoginID]),
													constraint [Administrator_Login_FK] foreign key ([LoginID]) references [Login]([IDLogin]),
);
go

create table [Blogpost]
(
    [IDBlogpost]		int				identity,		constraint [Blogpost_PK] primary key ([IDBlogpost]),
	[Title]				nvarchar(max)	not null,
	[Link]				nvarchar(max)	not null,
	[DatePublished]		datetimeoffset	not null,
	[Description]		nvarchar(max)	not null,
	[EncodedContent]	nvarchar(max)	not null,
    [ImagePath]			nvarchar(max)	not null,
);
go

create table [Category]
(
    [IDCategory]	int				identity,		constraint [Category_PK] primary key ([IDCategory]),
	[Name]			nvarchar(max)	not null,
);
go

create table [BlogpostCategoryLink]
(
	[IDBCLink]		int				identity,		constraint [BlogpostCategoryLink_PK] primary key ([IDBCLink]),
    [BlogpostID]	int				not null,		constraint [BlogpostCategoryLink_Blogpost_FK] foreign key ([BlogpostID]) references [Blogpost]([IDBlogpost]),
	[CategoryID]	int				not null,		constraint [BlogpostCategoryLink_Category_FK] foreign key ([CategoryID]) references [Category]([IDCategory]),
);
GO

-- ************************************************ Blogpost [C]reation ************************************************
create or alter proc [createBlogpost]
(
	@Title nvarchar(max),
    @Link nvarchar(max),
	@DatePublishedStr nvarchar(max),
    @Description nvarchar(max),
    @EncodedContent nvarchar(max),
    @ImagePath nvarchar(max),
	
	@BlogpostID int output
)
as
begin
	-- Minor performance boost basically, at least according to the docs
	-- Not really necessary though
	set NOCOUNT on
	
	declare @DatePublished datetimeoffset
	set @DatePublished = cast(@DatePublishedStr as datetimeoffset)

	insert into [Blogpost]([Title], [Link], [DatePublished], [Description], [EncodedContent], [ImagePath])
	values
	(@Title, @Link, @DatePublished, @Description, @EncodedContent, @ImagePath)

	-- Return
	set @BlogpostID = SCOPE_IDENTITY()
end
go

create or alter proc [linkBlogpostToCategory]
(
	@BlogpostID int,
    @CategoryName nvarchar(max)
)
as
begin
	declare @CategoryID int
	set @CategoryID = (select top 1 c.[IDCategory] from [Category] as c where c.[Name] = @CategoryName)

	if @CategoryID is null
	begin
		insert into [Category]([Name])
		values
		(@CategoryName)
		
		set @CategoryID = SCOPE_IDENTITY()
	end

	insert into [BlogpostCategoryLink]([BlogpostID], [CategoryID])
	values
	(@BlogpostID, @CategoryID)
end
go

-- ************************************************ Blogpost [R]ead / Select ************************************************
create or alter proc [selectBlogposts]
as
begin
	select
		bp.[IDBlogpost],
		bp.[Title],
		bp.[Description],
		bp.[Link],
		(cast (bp.[DatePublished] as nvarchar(max))) as 'DatePublishedStr',
		bp.[Description],
		bp.[EncodedContent],
		bp.[ImagePath]
	from [Blogpost] as bp
end
go

create or alter proc [selectCategories]
as
begin
	select
		cat.Name as 'CategoryName'
	from [Category] as cat
end
go

create or alter proc [selectBlogpost]
(
	@BlogpostID int
)
as
begin
	select
		bp.[IDBlogpost],
		bp.[Title],
		bp.[Description],
		bp.[Link],
		(cast (bp.[DatePublished] as nvarchar(max))) as 'DatePublishedStr',
		bp.[Description],
		bp.[EncodedContent],
		bp.[ImagePath]
	from [Blogpost] as bp
	where bp.IDBlogpost = @BlogpostID
end
go

create or alter proc [selectBlogpostCategories]
(
	@BlogpostID int
)
as
begin
	select cat.Name as 'CategoryName' from [Category] as cat
	inner join [BlogpostCategoryLink] as link on link.CategoryID = cat.IDCategory
	where link.BlogpostID = @BlogpostID
end
go

-- ************************************************ Blogpost [U]pdate /  Edit ************************************************
create or alter proc [updateBlogpostAndFlushCategories]
(
	@BlogpostID int,
	@Title nvarchar(max),
    @Link nvarchar(max),
	@DatePublishedStr nvarchar(max),
    @Description nvarchar(max),
    @EncodedContent nvarchar(max),
    @ImagePath nvarchar(max)
)
as
begin
	update [Blogpost]
	set
		[Title] = @Title,
		[Link] = @Link,
		[DatePublished] = cast(@DatePublishedStr as datetimeoffset),
		[Description] = @Description,
		[EncodedContent] = @EncodedContent,
		[ImagePath] = @ImagePath
	where [IDBlogpost] = @BlogpostID

	delete from [BlogpostCategoryLink]
	where [BlogpostID] = @BlogpostID

	delete from [Category]
	where [IDCategory] not in (select [CategoryID] from [BlogpostCategoryLink])
end
go

-- ************************************************ Blogpost [D]elete ************************************************
create or alter proc [deleteBlogpost]
(
	@BlogpostID int
)
as
begin
	delete from [BlogpostCategoryLink]
	where [BlogpostID] = @BlogpostID
	
	delete from [Blogpost]
	where [IDBlogpost] = @BlogpostID

	delete from [Category]
	where [IDCategory] not in (select [CategoryID] from [BlogpostCategoryLink])
end
go

create or alter proc [deleteAllBlogpostData]
as
begin
	delete from [BlogpostCategoryLink]
	delete from [Blogpost]
	delete from [Category]
end
go

-- ************************************************ User Login and Register ************************************************
create or alter proc [tryLogIn]
(
	@Alias nvarchar(max),
    @PasswordPlain nvarchar(max),
	
	@Exists bit output,
	@IsAdmin bit output
)
as
begin
	declare @TheRealLoginID int
	set  @TheRealLoginID =
	(
		select top 1 u.[IDLogin]
		from [Login] as u
		where u.[Alias] = @Alias and u.[PasswordPlain] = @PasswordPlain
	)
	
	set @Exists = 0
	set @IsAdmin = 0

	if (@TheRealLoginID is not null)
	begin
		set @Exists = 1

		if (@TheRealLoginID in (select a.[LoginID] from [Administrator] as a))
		begin
			set @IsAdmin = 1
		end
	end
end
go

create or alter proc [tryRegister]
(
	@Alias nvarchar(max),
    @PasswordPlain nvarchar(max),
	
	@Success bit output
)
as
begin
	set @Success = 0

	if (@Alias not in (select l.[Alias] from [Login] as l))
	begin
		insert into [Login]([Alias], [PasswordPlain])
		values
		(@Alias, @PasswordPlain)

		set @Success = 1
	end
end
go