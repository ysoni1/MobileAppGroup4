from db import db

class PetModel(db.Model):
    
    __tablename__ = 'pets'
    
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    sex = db.Column(db.String(1))
    type = db.Column(db.String)
    breed = db.Column(db.String)
    bod = db.Column(db.Date)
    
    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    owner = db.relationship('UserModel')

    def __init__(self, name, sex, type, breed, owner_id, bod):
        self.name = name
        self.sex = sex
        self.type = type
        self.breed = breed
        self.owner_id = owner_id
        self.bod = bod

    def json(self):
        return { 'id'    : self.id,
                 'name'  : self.name, 
                 'sex'   : self.sex, 
                 'type'  : self.type,
                 'breed' : self.breed,
                 'owner' : self.owner_id,
                 'bod'   : self.bod.__str__()
               }

    @classmethod
    def find_by_name(cls, name, owner_id):
        return PetModel.query.filter_by(name = name, owner_id = owner_id).first()

    @classmethod
    def find_by_id(cls, _id, owner_id):
        return PetModel.query.filter_by(id = _id, owner_id = owner_id).first()

    @classmethod
    def find_by_owner(cls, owner_id):
        return PetModel.query.filter_by(owner_id = owner_id).all()

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()


