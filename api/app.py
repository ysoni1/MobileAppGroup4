from flask import Flask
from flask_restplus import Resource, Api, Namespace, fields
from flask_cors import CORS 
from flask_jwt import JWT
from db import db

from security import authenticate, identity
from resources.user import User
from resources.user import UserList

from resources.pet import PetAPI
from resources.pet import Pet
from resources.pet import PetList

from resources.doctor import DoctorAPI
from resources.doctor import Doctor
from resources.doctor import DoctorList

from resources.task import TaskAPI
from resources.task import Task
from resources.task import TaskList

from resources.advisor import AdvisorAPI
from resources.advisor import Advisor
from resources.advisor import AdviceList

app = Flask(__name__)
app.config.from_object('config')

CORS(app)
api = Api(app, version='1.0', title='PetMan App API',  description='Flask API PetMan Application - GSU 2019')

db.init_app(app)

@app.before_first_request
def create_tables():
    db.create_all()    

jwt = JWT(app, authenticate, identity)

api.add_resource(User, '/register')
api.add_resource(User, '/user/<string:username>')
api.add_resource(UserList, '/users/')

api.add_resource(PetAPI, '/pet')
api.add_resource(Pet, '/pet/<string:_id>/<string:owner>')
api.add_resource(PetList, '/pets/<string:owner>')

api.add_resource(DoctorAPI, '/doctor')
api.add_resource(Doctor, '/doctor/<string:_id>/<string:pet_id>')
api.add_resource(DoctorList, '/doctors/<string:pet_id>')

api.add_resource(TaskAPI, '/task')
api.add_resource(Task, '/task/<string:_id>/<string:owner>')
api.add_resource(TaskList, '/tasks/<string:owner>')

api.add_resource(AdvisorAPI, '/advice')
api.add_resource(Advisor, '/advice/<string:_id>/<string:owner>')
api.add_resource(AdviceList, '/advices/<string:owner>')

if __name__ == '__main__':
    app.run(debug=True)
