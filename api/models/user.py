from db import db

class UserModel(db.Model):
    
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String)
    password = db.Column(db.String)
    first_name = db.Column(db.String)
    last_name = db.Column(db.String)
    email = db.Column(db.String)

    pets = db.relationship('PetModel', lazy='dynamic')

    def __init__(self, username, password, first_name, last_name, email):
        self.username = username
        self.password = password
        self.first_name = first_name
        self.last_name = last_name
        self.email = email

    def json(self):
        return { 'id'        : self.id,
                 'firstname' : self.first_name, 
                 'lastname'  : self.last_name, 
                 'email'     : self.email,
                 'username'  : self.username
               }

    @classmethod
    def find_by_username(cls, username):
        return UserModel.query.filter_by(username = username).first()

    @classmethod
    def find_by_id(cls, _id):
        return UserModel.query.filter_by(id = _id).first()

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()


