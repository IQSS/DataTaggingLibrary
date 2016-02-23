package edu.harvard.iq.datatags.prettyPrinter;
public class CommentClass {
	private String _bracketWord;
	private String _comment;
	private int _kindOfComment; //0 - for line comment, 1 - for block comment
	private String _matchWordForBlockCommet="";
	public CommentClass(String bracket, String Comment,int kindOfComment)
	{
		this._bracketWord=bracket;
		this._comment = Comment;
		this._kindOfComment=kindOfComment;
	}
        
        public CommentClass(String bracket, String Comment,int kindOfComment, String matchWordForBlockComment)
	{
		this._bracketWord=bracket;
		this._comment = Comment;
		this._kindOfComment=kindOfComment;
                _matchWordForBlockCommet=matchWordForBlockComment;
	}
	public String getComment()
	{
		return(this._comment);
	}
	public String getBracket()
	{
		return(this._bracketWord);
	}
	public int getKindOfComment()
	{
		return(this._kindOfComment);
	}
        public String getMatchWordForBlockComment()
        {
            return(this._matchWordForBlockCommet);
        }
}
