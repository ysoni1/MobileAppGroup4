from db import db

class AdvisorModel(db.Model):
    __tablename__ = 'advisor'

    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String)
    advice_date = db.Column(db.Date)
    complete = db.Column(db.Boolean)

    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    owner = db.relationship('UserModel')

    def __init__(self, description, advice_date, complete, owner_id):
        self.description = description
        self.advice_date = advice_date
        self.complete = complete
        self.owner_id = owner_id
    
    def json(self):
        return { 'id'          : self.id,
                 'description' : self.description, 
                 'advice_date' : self.advice_date.__str__(),
                 'complete'    : self.complete,
                 'owner_id'    : self.owner_id        
        }
    
    @classmethod
    def find_by_id(cls, _id, owner_id):
        return cls.query.filter_by(id = _id, owner_id = owner_id).first()

    @classmethod
    def find_by_owner(cls, owner_id):
         return cls.query.filter_by(owner_id = owner_id).all()
    
    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

