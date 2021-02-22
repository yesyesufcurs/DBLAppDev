from setuptools import setup

setup(
    name='backendserver',
    packages=['backendserver'],
    include_package_data=True,
    install_requires=[
        'flask',
    ],
)