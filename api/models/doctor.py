from db import db

class DoctorModel(db.Model):
    __tablename__ = 'doctors'
    
    id = db.Column(db.Integer, primary_key=True)
    clinic = db.Column(db.String)
    address = db.Column(db.String)
    city = db.Column(db.String)
    state = db.Column(db.String)
    zipcode = db.Column(db.Integer)
    phone = db.Column(db.String)
    hours = db.Column(db.String)
    contact = db.Column(db.String)

    pet_id = db.Column(db.Integer, db.ForeignKey('pets.id'))
    pet = db.relationship('PetModel')

    def __init__(self, clinic, address, city, state, zipcode, phone, hours, contact, pet_id):
        self.clinic = clinic
        self.address = address
        self.city = city
        self.state = state
        self.zipcode = zipcode
        self.phone = phone
        self.hours = hours
        self.contact = contact
        self.pet_id = pet_id

    def json(self):
        return { 'id'      : self.id,
                 'clinic'  : self.clinic, 
                 'address' : self.address, 
                 'city'    : self.city,
                 'state'   : self.state,
                 'zipcode' : self.zipcode, 
                 'phone'   : self.phone,
                 'hours'   : self.hours,
                 'contact' : self.contact,
                 'pet_id'  : self.pet_id
               }

    @classmethod
    def find_by_name(cls, name, pet_id):
        return cls.query.filter_by(clinic = name, pet_id = pet_id).first()

    @classmethod
    def find_by_id(cls, _id, pet_id):
        return cls.query.filter_by(id = _id, pet_id = pet_id).first()

    @classmethod
    def find_by_pet(cls, pet_id):
        return cls.query.filter_by(pet_id = pet_id).all()

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()
    

                
        


